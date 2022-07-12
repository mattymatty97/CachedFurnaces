package com.mattymatty.furnace.mixins;

import com.mattymatty.furnace.FurnaceItem;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Shadow
    public static Map<Item, Integer> createFuelTimeMap() {
        return null;
    }

    private static Map<Item, Integer> cachedFuelMap;

    private static boolean initialize = false;

    @Inject(method = "createFuelTimeMap", at = @At("HEAD"), cancellable = true)
    private static void cacheFuelTimes(CallbackInfoReturnable<Map<Item, Integer>> cir){
        if (initialize)
            return;
        if (cachedFuelMap == null) {
            initialize = true;
            cachedFuelMap = createFuelTimeMap();
            initialize = false;
        }

        cir.setReturnValue(cachedFuelMap);
    }

    @SuppressWarnings({"rawtypes"})
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"))
    private static Optional<?> cacheRecipe1(RecipeManager instance, RecipeType type, Inventory inventory, World world){
        return _cacheRecipe(instance, type, inventory, world);
    }

    @SuppressWarnings({"rawtypes"})
    @Redirect(method = "getCookTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"))
    private static Optional<?> cacheRecipe2(RecipeManager instance, RecipeType type, Inventory inventory, World world){
        return _cacheRecipe(instance, type, inventory, world);
    }

    @NotNull
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Optional<Recipe<?>> _cacheRecipe(RecipeManager instance, RecipeType type, Inventory inventory, World world) {
        ItemStack cachedStack = inventory.getStack(0);
        if (cachedStack == null)
            return Optional.empty();
        Optional<Recipe<?>> ret = Optional.ofNullable(((FurnaceItem) (Object) cachedStack).getCachedRecipe());
        if (ret.isPresent() && ret.get().getType() == type)
            return ret;
        else
            ((FurnaceItem) (Object) cachedStack).setCachedRecipe(null);
        ret = instance.getFirstMatch(type, inventory, world);
        ret.ifPresent(o -> ((FurnaceItem) (Object) cachedStack).setCachedRecipe(o));
        return ret;
    }


}
