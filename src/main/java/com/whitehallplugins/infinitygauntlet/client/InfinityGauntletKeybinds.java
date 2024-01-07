package com.whitehallplugins.infinitygauntlet.client;

import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class InfinityGauntletKeybinds {
    public static final KeyBinding CHANGE_POWER = new KeyBinding(
            "key.infinitygauntlet.changepower",
            GLFW.GLFW_KEY_H,
            "key.category.infinitygauntlet"
    );
}
