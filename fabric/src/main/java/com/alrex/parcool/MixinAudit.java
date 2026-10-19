package com.alrex.parcool;

import org.spongepowered.asm.mixin.MixinEnvironment;

/**
 * Development check: with {@code -Dparcool.mixinAudit=true} the game loads every class ParCool's
 * mixins target, prints the result and exits. A target that changed between Minecraft versions then
 * fails here, in CI, instead of in a player's game. Does nothing in normal play.
 */
final class MixinAudit {
    private MixinAudit() {
    }

    static void runIfRequested(String side) {
        if (!Boolean.getBoolean("parcool.mixinAudit")) return;
        try {
            MixinEnvironment.getCurrentEnvironment().audit();
            System.out.println("PARCOOL_MIXIN_AUDIT_OK " + side);
            System.exit(0);
        } catch (Throwable t) {
            System.out.println("PARCOOL_MIXIN_AUDIT_FAILED " + side);
            t.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
