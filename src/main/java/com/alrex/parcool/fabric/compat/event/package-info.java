/**
 * Minimal Fabric-side adapters for the NeoForge events ParCool 4.0 handles.
 *
 * <p>Each class keeps the simple name and only the members that upstream ParCool calls, so the
 * upstream handler code compiles unchanged after {@code porting/neoforge_to_fabric.py} rewrites
 * its imports. The events are created and dispatched by
 * {@link com.alrex.parcool.fabric.FabricEventBridge} (from Fabric API callbacks and ParCool's
 * mixins). They are not a general event system: there is no bus, no priorities and no listener
 * discovery.
 */
package com.alrex.parcool.fabric.compat.event;
