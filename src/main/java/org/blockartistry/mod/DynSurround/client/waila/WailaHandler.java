/* This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

//
// Thank you Darkhax!
//
// https://github.com/Minecraft-Forge-Tutorials/Waila-Integration
//

package org.blockartistry.mod.DynSurround.client.waila;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.footsteps.Footsteps;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IBlockMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public final class WailaHandler implements IWailaDataProvider {

	private List<String> gatherText(final ItemStack stack, final List<String> text, final IWailaDataAccessor accessor,
			final IWailaConfigHandler config) {

		if (stack == null || stack.getItem() == null)
			return text;

		text.add(EnumChatFormatting.GOLD + "dsurround");
		
		final Item item = stack.getItem();
		final String blockName = GameData.getItemRegistry().getNameForObject(item);
		if (!StringUtils.isEmpty(blockName)) {
			final StringBuilder builder = new StringBuilder();
			builder.append(blockName);
			if (stack.getHasSubtypes())
				builder.append(':').append(stack.getItemDamage());
			builder.append('[').append(accessor.getMetadata()).append(']');
			text.add(builder.toString());
		}

		if (Footsteps.INSTANCE != null) {
			final IBlockMap bm = Footsteps.INSTANCE.getBlockMap();
			if (bm != null) {
				bm.collectData(accessor.getBlock(), accessor.getMetadata(), text);
			}
		}
		return text;
	}

	@Optional.Method(modid = "Waila")
	public static void callbackRegister(final IWailaRegistrar register) {
		ModLog.info("Registering Waila handler...");
		final WailaHandler instance = new WailaHandler();
		register.registerTailProvider(instance, Block.class);
	}

	public WailaHandler() {

	}

	@Override
	@Optional.Method(modid = "Waila")
	public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip,
			final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

		return gatherText(itemStack, currenttip, accessor, config);
	}

	@Override
	@Optional.Method(modid = "Waila")
	public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip,
			final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

		return gatherText(itemStack, currenttip, accessor, config);
	}

	@Override
	@Optional.Method(modid = "Waila")
	public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip,
			final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

		return gatherText(itemStack, currenttip, accessor, config);
	}

	@Override
	public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

		return null;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP arg0, final TileEntity arg1, final NBTTagCompound arg2,
			final World arg3, final int arg4, final int arg5, final int arg6) {
		return null;
	}

	public static void register() {
		if (ModOptions.getEnableDebugLogging())
			FMLInterModComms.sendMessage("Waila", "register", WailaHandler.class.getName() + ".callbackRegister");
	}
}