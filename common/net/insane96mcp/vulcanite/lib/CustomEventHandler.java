package net.insane96mcp.vulcanite.lib;

import net.insane96mcp.vulcanite.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.collection.generic.BitOperations.Int;

public class CustomEventHandler {
	
	@SubscribeEvent
	public static void LivingHurtEvent(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			DamageSource source = event.getSource();
			DamageSource[] validSources = new DamageSource[] {
				DamageSource.IN_FIRE, 
				DamageSource.ON_FIRE, 
				DamageSource.HOT_FLOOR, 
				DamageSource.LAVA
			};
			
			for (DamageSource damageSource : validSources) {
				if (source == damageSource) {
					float amount = event.getAmount();
					
					ItemStack[] armorList = new ItemStack[] {new ItemStack(ModItems.vulcaniteHelmetItem), new ItemStack(ModItems.vulcaniteChestplateItem), new ItemStack(ModItems.vulcaniteLeggingsItem), new ItemStack(ModItems.vulcaniteBootsItem)};
				    int gearCounter = 0;
				    Iterable<ItemStack> playerArmor = player.getArmorInventoryList();
				    for (ItemStack armorPiece : playerArmor) {
				    	for (ItemStack armorL : armorList) {
					        if (ItemStack.areItemsEqualIgnoreDurability(armorPiece, armorL)) {
								gearCounter++;
								break;
							}
						}
					}
				    if(gearCounter >= 1) {
				    	float reductionPerPiece = 22.5f;
				    	float percentageReduction = gearCounter * reductionPerPiece;
				    	amount -= (amount / 100 * percentageReduction);
				        event.setAmount(amount);
				    }
				}
			}
		}
	}
}
