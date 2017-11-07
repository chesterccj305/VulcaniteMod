package net.insane96mcp.vulcanite.events;

import net.insane96mcp.vulcanite.init.ModItems;
import net.insane96mcp.vulcanite.lib.Properties;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingHurt {

	@SubscribeEvent
	public static void LivingHurtEvent(LivingHurtEvent event) {
		OnPlayerHurt(event);
		OnPlayerDamageEntity(event);
	}
	
	static ItemStack[] armorList = new ItemStack[] {
		new ItemStack(ModItems.vulcaniteHelmetItem), 
		new ItemStack(ModItems.vulcaniteChestplateItem), 
		new ItemStack(ModItems.vulcaniteLeggingsItem), 
		new ItemStack(ModItems.vulcaniteBootsItem)
	};
	
	public static void OnPlayerHurt(LivingHurtEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		DamageSource source = event.getSource();
		DamageSource[] validSources = new DamageSource[] {
			DamageSource.inFire,
			DamageSource.onFire,
			DamageSource.hotFloor,
			DamageSource.lava
		};
		
		float[] materialPerPiece = new float[] { 5, 8, 7, 4 };
		
		for (DamageSource damageSource : validSources) {
			if (source != damageSource) 
				continue;
			
			float amount = event.getAmount();
			
		    int materialsUsed = 0;
		    Iterable<ItemStack> playerArmor = player.getArmorInventoryList();
		    for (ItemStack armorPiece : playerArmor) {
		    	for (int i = 0; i < armorList.length; i++) {
			        if (ItemStack.areItemsEqualIgnoreDurability(armorPiece, armorList[i])) {
						materialsUsed += materialPerPiece[i];
						break;
					}
				}
			}
		    
		    if (materialsUsed >= 1) {
		    	float maxReduction = Properties.Armor.hotSourceDamageReduction / 100f;
		    	float reductionPerMaterial = maxReduction / 24f;
		    	float percentageReduction = reductionPerMaterial * materialsUsed;
		    	amount = amount * (1f - percentageReduction);
		        event.setAmount(amount);
		    }
		}
	}
	
	private static ItemStack vulcaniteSword = new ItemStack(ModItems.vulcaniteSwordItem);
	
	public static void OnPlayerDamageEntity(LivingHurtEvent event) {
		if (event.getSource().damageType != "player")
			return;
		
		EntityPlayerMP player = (EntityPlayerMP) event.getSource().getEntity();
		ItemStack heldItem = player.getHeldItemMainhand();
		if (!ItemStack.areItemsEqualIgnoreDurability(heldItem, vulcaniteSword))
			return;
		
		NBTTagList enchantments = heldItem.getEnchantmentTagList();
		
		if (enchantments == null)
			return;
		
		int fireAspectLevel = 0;
		for (int i = 0; i < enchantments.tagCount(); i++) {
			if (enchantments.getCompoundTagAt(i).getShort("id") == Enchantment.getEnchantmentID(Enchantments.FIRE_ASPECT))
				fireAspectLevel = enchantments.getCompoundTagAt(i).getShort("lvl");
		}
		
		if (fireAspectLevel == 0)
			return;
		
		Entity target = event.getEntity();
		if (!(target instanceof EntityLivingBase))
			return;
		
		EntityLivingBase entity = (EntityLivingBase)target;
		if (!entity.isImmuneToFire())
			return;
		
		float damageDealth = event.getAmount();
		float bonusDamageDealth = damageDealth * ((Properties.Tools.bonusDamagePerFALevel / 100f) * fireAspectLevel);
		
		event.setAmount(damageDealth + bonusDamageDealth);
	}
}
