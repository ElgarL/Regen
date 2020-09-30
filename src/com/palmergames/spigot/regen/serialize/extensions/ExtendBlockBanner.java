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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;

import com.palmergames.spigot.regen.serialize.SerializedBlock;
import com.palmergames.spigot.regen.serialize.SerializedObject;

/**
 * @author ElgarL
 *
 */
public class ExtendBlockBanner extends SerializedBlock {

	private DyeColor baseColor = null;
	private List<Pattern> patterns = new ArrayList<Pattern>();

	public ExtendBlockBanner() {}
	/**
	 * Constructor to create a ExtendBlockBanner from a Block.
	 * 
	 * @param <T>	extends Block
	 * @param block	the Block to store.
	 */
	public <T extends Block> ExtendBlockBanner(T block) {

		super(block);

		Banner state = (Banner) block.getState();

		this.baseColor = state.getBaseColor();
		this.patterns = state.getPatterns();
	}

	@Override
	public BlockState regen() {

		Banner state = (Banner) super.regen();

		state.setBaseColor(getBaseColor());
		state.setPatterns(getPatterns());

		state.update(true);

		return state.getBlock().getState();
	}

	@Override
	public ExtendBlockBanner clone() {

		ExtendBlockBanner clone = null;

		clone = (ExtendBlockBanner) super.clone();

		clone.baseColor = this.baseColor;
		clone.patterns.addAll(this.patterns);

		return clone;
	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> result = super.serialize();

		if (getBaseColor() != null) result.put("colour", getBaseColor().name());
		if (!getPatterns().isEmpty()) result.put("patterns", getPatterns());

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

		super.deserialize(map);

		if (map.containsKey("colour")) this.baseColor = DyeColor.valueOf((String) map.get("colour"));
		if (map.containsKey("patterns")) this.patterns = (List<Pattern>) map.get("patterns");

		return (Z) this;
	}

	/**
	 * @return the baseColor
	 */
	public DyeColor getBaseColor() {

		return baseColor;
	}


	/**
	 * @return the patterns
	 */
	public List<Pattern> getPatterns() {

		return patterns;
	}

}
