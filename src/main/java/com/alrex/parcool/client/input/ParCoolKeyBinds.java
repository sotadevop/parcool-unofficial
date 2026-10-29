package com.alrex.parcool.client.input;

import com.alrex.parcool.extern.AdditionalMods;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.ClientTickEvent;
import com.alrex.parcool.fabric.compat.event.RegisterKeyMappingsEvent;
import net.minecraft.resources.Identifier;
import com.alrex.parcool.ParCool;
import java.util.IdentityHashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class ParCoolKeyBinds {
    public interface IStateProvider {
        InputState state();
    }

    public record Input(KeyMapping key, InputState state) implements IStateProvider {
		private static Input from(KeyMapping key) {
			return new Input(key, new InputState());
		}

		private void update() {
			state.update(key);
		}
	}

    public record LogicalInput(BooleanSupplier keyDownSupplier, InputState state) implements IStateProvider {
        private static LogicalInput from(BooleanSupplier keyDownSupplier) {
            return new LogicalInput(keyDownSupplier, new InputState());
        }

        private void update() {
            state.update(keyDownSupplier.getAsBoolean());
        }
    }

	public static class InputState {
        private boolean down;
		private int pressedDurationTick = 0;
		private int notPressedDurationTick = 0;
		private int previousPressedDurationTick = 0;
		private int previousNotPressedDurationTick = 0;

		private void update(KeyMapping key) {
            update(key.isDown() && isModifierHeld(key));
        }

        private void update(boolean keyDown) {
            down = keyDown;
            if (keyDown) {
				pressedDurationTick++;
				if (pressedDurationTick == 0) previousNotPressedDurationTick = notPressedDurationTick;
				notPressedDurationTick = -1;
			} else {
				notPressedDurationTick++;
				if (notPressedDurationTick == 0) previousPressedDurationTick = pressedDurationTick;
				pressedDurationTick = -1;
			}
		}

        public boolean isDown() {
            return down;
        }

		public int getPressedDuration() {
			return pressedDurationTick;
		}

		public int getNotPressedDuration() {
			return notPressedDurationTick;
		}

		public int getPreviousNotPressedDurationTick() {
			return previousNotPressedDurationTick;
		}

		public int getPreviousPressedDurationTick() {
			return previousPressedDurationTick;
		}

		public boolean isJustPressed() {
			return pressedDurationTick == 0;
		}

		public boolean isJustReleased() {
			return notPressedDurationTick == 0;
		}
	}

	private static final ArrayList<Input> REGISTERED_KEYS = new ArrayList<>();
    private static final ArrayList<LogicalInput> LISTENING_KEYS = new ArrayList<>();

	/**
	 * Fabric port: NeoForge's KeyModifier (Ctrl+P, Alt+P) does not exist in vanilla. The binding is
	 * registered without it and only counts as pressed while the modifier key is held.
	 */
	private static final Map<KeyMapping, Integer> REQUIRED_MODIFIER = new IdentityHashMap<>();

	private static KeyMapping withModifier(KeyMapping key, int modifierKey) {
		REQUIRED_MODIFIER.put(key, modifierKey);
		return key;
	}

	private static boolean isModifierHeld(KeyMapping key) {
		var modifier = REQUIRED_MODIFIER.get(key);
		return modifier == null || InputConstants.isKeyDown(modifier);
	}

	private static Input register(KeyMapping key) {
		var input = Input.from(key);
		REGISTERED_KEYS.add(input);
		return input;
	}

    private static LogicalInput listen(BooleanSupplier keyDownSupplier) {
        var input = LogicalInput.from(keyDownSupplier);
		LISTENING_KEYS.add(input);
		return input;
	}

	// 26.3: categories are registered objects; the label is translated from
	// "key.category.<namespace>.<path>", here key.category.parcool.parcool.
	public static final KeyMapping.Category KEY_CATEGORY =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "parcool"));

	public static final Input FAST_MOVE = register(new KeyMapping("key.parcool.fast_move", InputConstants.KEY_LCONTROL, KEY_CATEGORY));
	public static final Input CRAWL = register(new KeyMapping("key.parcool.crawl", InputConstants.KEY_C, KEY_CATEGORY));
    public static final Input HANG = register(new KeyMapping("key.parcool.hang", InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_RIGHT, KEY_CATEGORY));
	public static final Input SLIDE_DOWN = register(new KeyMapping("key.parcool.slide_down", InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_RIGHT, KEY_CATEGORY));
    public static final Input DODGE = register(new KeyMapping("key.parcool.dodge", InputConstants.KEY_R, KEY_CATEGORY));
    public static final Input BREAKFALL = register(new KeyMapping("key.parcool.breakfall", InputConstants.KEY_R, KEY_CATEGORY));
	public static final Input HORIZONTAL_WALL_RUN = register(new KeyMapping("key.parcool.horizontal_wall_run", InputConstants.KEY_R, KEY_CATEGORY));
    public static final Input HIDE_IN_BLOCK = register(new KeyMapping("key.parcool.hide_in_block", InputConstants.KEY_C, KEY_CATEGORY));
    public static final Input OPEN_SETTING_SKILLTREE = register(withModifier(new KeyMapping("key.parcool.open_setting_skilltree", InputConstants.Type.KEYBOARD, InputConstants.KEY_P, KEY_CATEGORY), InputConstants.KEY_LALT));
	public static final Input ENABLE = register(withModifier(new KeyMapping("key.parcool.enable", InputConstants.Type.KEYBOARD, InputConstants.KEY_P, KEY_CATEGORY), InputConstants.KEY_LCONTROL));

    public static final LogicalInput JUMP = listen(Minecraft.getInstance().options.keyJump::isDown);
    public static final LogicalInput USE_ITEM = listen(Minecraft.getInstance().options.keyUse::isDown);
    public static final LogicalInput ATTACK = listen(Minecraft.getInstance().options.keyAttack::isDown);
	public static final LogicalInput SHIFT = listen(Minecraft.getInstance().options.keyShift::isDown);
    public static final LogicalInput MOVEMENT_FORWARD = listen(Minecraft.getInstance().options.keyUp::isDown);
    public static final LogicalInput MOVEMENT_BACK = listen(Minecraft.getInstance().options.keyDown::isDown);
    public static final LogicalInput MOVEMENT_RIGHT = listen(Minecraft.getInstance().options.keyRight::isDown);
    public static final LogicalInput MOVEMENT_LEFT = listen(Minecraft.getInstance().options.keyLeft::isDown);

    public static InputState getMovementInput(LogicalMovement movement) {
		var input = AdditionalMods.getLogicalKey(movement);
		return input == null ? getStandardInput(movement).state : input.state;
    }

	public static LogicalInput getStandardInput(LogicalMovement movement) {
		return switch (movement) {
			case LEFT -> MOVEMENT_LEFT;
			case RIGHT -> MOVEMENT_RIGHT;
			case FORWARD -> MOVEMENT_FORWARD;
			case BACKWARD -> MOVEMENT_BACK;
		};
	}

    public static void onTick(ClientTickEvent.Pre event) {
		for (var input : REGISTERED_KEYS) {
			input.update();
		}
		for (var input : LISTENING_KEYS) {
			input.update();
		}
	}

	public static void registerAll(RegisterKeyMappingsEvent event) {
		for (var input : REGISTERED_KEYS) {
			event.register(input.key);
		}
		REGISTERED_KEYS.trimToSize();
		LISTENING_KEYS.trimToSize();
	}
}
