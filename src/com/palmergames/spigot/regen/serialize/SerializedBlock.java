/*
 *  Regen - A plug-in for Spigot/Bukkit based Minecraft servers.
 *  Copyright (C) 2020  ElgarL
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.palmergames.spigot.regen.serialize;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Lectern;
import org.bukkit.block.Jukebox;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A representation of a Block we can
 * serialize/deserialize and use to
 * replace a block in the World.
 * 
 * @author ElgarL
 *
 */
public class SerializedBlock extends SerializedObject {

	protected BlockData blockData = null;

	public SerializedBlock() {}
	/**
	 * Constructor to create a SerializedBlock from a Block.
	 * 
	 * @param <T>	extends Block
	 * @param block	the Block to store.
	 */
	public <T extends Block> SerializedBlock(T block) {

		BlockState state = block.getState();
		this.loc = state.getLocation();
		this.blockData = state.getBlockData();

		/*
		 * Read Inventories.
		 */
		Inventory container = null;

		switch (state.getType()) {

		case LECTERN:
			container = ((Lectern) state).getSnapshotInventory();
			break;

		case JUKEBOX:
			ItemStack disc = ((Jukebox)state).getRecord();
			if (disc != null) this.items.add(disc);
			break;

		default:
			if (state instanceof Container)
				container = ((Container) state).getSnapshotInventory();
		}	
		/*
		 * Store this Inventory eliminating null.
		 */
		if ((container != null) && !container.isEmpty()) {

			for (ItemStack itemStack : container.getContents()) {
				if (itemStack != null)
					this.items.add(itemStack);
			}
		}
	}

	@Override
	public BlockState regen() {

		Block block = getLocation().getBlock();
		BlockState state = block.getState();

		if (block.getType() != this.getType()) {

			state.setType(this.getType());
			state.setBlockData(this.getBlockData());
			/*
			 * Change the block with physics
			 * if conditions are met.
			 */
			state.update(true, applyPhysics(block));

			// Refresh our snapshot.
			state = state.getBlock().getState();

			/*
			 * Refill Inventories.
			 */
			if (this.hasInventory()) {

				Inventory container = null;

				switch (state.getType()) {

				case LECTERN:
					container = ((Lectern) state).getInventory();
					break;

				case JUKEBOX:
					((Jukebox) state).setRecord(this.items.get(0));
					state.update(true,  false);
					break;

				default:
					if (state instanceof Container)
						container = ((Container) state).getInventory();
				}

				if (container != null) container.setContents(this.getInventory());
			}
		}
		return state.getBlock().getState();
	}

	@Override
	public SerializedBlock clone() {

		SerializedBlock clone = null;

		clone = (SerializedBlock) super.clone();
		clone.blockData = this.blockData.clone();

		return clone;
	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> result = super.serialize();

		result.put("blockData", blockData.getAsString());

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

		super.deserialize(map);

		this.blockData = Bukkit.createBlockData((String)map.get("blockData"));

		return (Z) this;
	}

	/**
	 * @return	the Material for this Block.
	 */
	public Material getType() {

		return this.blockData.getMaterial();
	}

	/**
	 * @return	the BlockData for this Block.
	 */
	public BlockData getBlockData() {

		return this.blockData;
	}

	/**
	 * Determine whether we need to apply
	 * physics when replacing this block.
	 * 
	 * @param block	the Block we are replacing.
	 * @return		true if applying physics.
	 */
	private boolean applyPhysics(Block block) {

		if (Tag.FENCES.isTagged(getType())) return true;
		if (block.isLiquid()) return true;

		return false;
	}

}
