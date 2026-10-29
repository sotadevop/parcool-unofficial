package com.alrex.parcool.client.md;

import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.util.List;

@Environment(EnvType.CLIENT)
public interface MarkdownParagraph {
    record Image(Identifier spriteLocation, @Nullable String caption) implements MarkdownParagraph {
        public static Image from(Identifier sprite, @Nullable String caption) {
            return new Image(sprite, caption);
        }
    }

    class HorizontalLine implements MarkdownParagraph {
    }

    record OrderedList(List<MarkdownParagraph.Text> items) implements MarkdownParagraph {
    }

    record UnOrderedList(List<MarkdownParagraph.Text> items) implements MarkdownParagraph {
    }

    record Heading(int level, List<MarkdownText> text) implements MarkdownParagraph {
    }

    record Text(List<MarkdownText> text) implements MarkdownParagraph {
    }

    /// `<recipe id="parcool:parcool_guide"/>`
    record ExtensionMCRecipe(Identifier recipeId) implements MarkdownParagraph {
    }

    /// `<brewing-recipe input="minecraft:awkward_potion" ingredient="minecraft:chicken"/>`
    record ExtensionMCBrewingRecipe(Identifier potionId) implements MarkdownParagraph {
    }
}
