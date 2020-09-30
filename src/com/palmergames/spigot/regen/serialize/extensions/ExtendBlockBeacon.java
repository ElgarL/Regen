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
package com.palmergames.spigot.regen.serialize.extensions;

import java.util.Map;

import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.potion.PotionEffectType;

import com.palmergames.spigot.regen.serialize.SerializedBlock;
import com.palmergames.spigot.regen.serialize.SerializedObject;

/**
 * @author ElgarL
 *
 */
public class ExtendBlockBeacon extends SerializedBlock {

	PotionEffectType primary = null;
	PotionEffectType secondary = null;

	public ExtendBlockBeacon () {}
	/**
	 * Constructor to create a ExtendBlockBeacon from a Block.
	 * 
	 * @param <T>	extends Block
	 * @param block	the Block to store.
	 */
	public <T extends Block> ExtendBlockBeacon (T block) {

		super(block);

		Beacon state = (Beacon) block.getState();

		if (state.getPrimaryEffect() != null) this.primary = state.getPrimaryEffect().getType();
		if (state.getSecondaryEffect() != null) this.secondary = state.getSecondaryEffect().getType();
	}

	@Override
	public BlockState regen() {

		Beacon state = (Beacon) super.regen();

		state.setPrimaryEffect(primary);
		state.setSecondaryEffect(secondary);

		state.update(true);

		return state.getBlock().getState();
	}

	@Override
	public ExtendBlockBeacon clone() {

		ExtendBlockBeacon clone = null;

		clone = (ExtendBlockBeacon) super.clone();

		clone.primary = this.primary;
		clone.secondary = this.secondary;

		return clone;
	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> result = super.serialize();

		if (this.primary != null) result.put("primary", this.primary);
		if (this.secondary != null) result.put("secondary", this.secondary);

		return result;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

		super.deserialize(map);

		if (map.containsKey("primary")) this.primary = PotionEffectType.getByName((String) map.get("primary"));
		if (map.containsKey("secondary")) this.secondary = PotionEffectType.getByName((String) map.get("secondary"));

		return (Z) this;
	}

	/**
	 * @return the primary
	 */
	public PotionEffectType getPrimary() {

		return primary;
	}

	/**
	 * @return the secondary
	 */
	public PotionEffectType getSecondary() {

		return secondary;
	}
}
