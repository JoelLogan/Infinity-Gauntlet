package com.whitehallplugins.infinitygauntlet.events;

import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TooltipUpdateEvent implements ItemTooltipCallback {

    @Override
    public void getTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> list) {
        String IGKey = "item.infinitygauntlet";
        if (itemStack.getItem().getTranslationKey().contains(IGKey)) {
            String key = itemStack.getItem().getTranslationKey();
            if (key.contains("replica")) {
                list.add(Text.translatable("item.infinitygauntlet.replicatooltip").formatted(Formatting.GRAY));
            }
            else if (key.equals(IGKey + ".gauntlet.gauntlet")) {
                list.add(Text.translatable("item.infinitygauntlet.gauntlet.gauntlet.tooltip1",
                        Text.translatable("item.infinitygauntlet.gauntlet.gauntlet.power" + Gauntlet.getCustomModelData(itemStack))).formatted(Formatting.GOLD));
            }
        }
    }
}
