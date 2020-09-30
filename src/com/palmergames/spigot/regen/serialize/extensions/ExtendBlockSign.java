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

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.palmergames.spigot.regen.serialize.SerializedBlock;
import com.palmergames.spigot.regen.serialize.SerializedObject;

/**
 * @author ElgarL
 *
 */
public class ExtendBlockSign extends SerializedBlock {

	String[] lines = null;
	private DyeColor colour = null;

	public ExtendBlockSign() {}
	/**
	 * Constructor to create a ExtendBlockSign from a Block.
	 * 
	 * @param <T>	extends Block
	 * @param block	the Block to store.
	 */
	public <T extends Block> ExtendBlockSign(T block) {

		super(block);

		Sign state = (Sign) block.getState();

		this.lines = state.getLines();
		this.colour = state.getColor();
	}

	@Override
	public BlockState regen() {

		Sign state = (Sign) super.regen();

		if (lines != null && lines.length > 0) {
			for (int i = 0; i < lines.length; i++) {
				state.setLine(i, lines[i]);
			}
		}

		if (colour != null) state.setColor(colour);

		state.update(true, false);

		return state.getBlock().getState();
	}

	@Override
	public ExtendBlockSign clone() {

		ExtendBlockSign clone = null;

		clone = (ExtendBlockSign) super.clone();
		clone.lines = this.lines.clone();
		clone.colour = this.colour;

		return clone;
	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> result = super.serialize();

		if (this.lines != null && this.lines.length > 0) result.put("lines", lines);
		if (this.colour != null) result.put("colour", this.colour.name());

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

		super.deserialize(map);

		if (map.containsKey("lines")) this.lines = (String[]) map.get("lines");
		if (map.containsKey("colour")) this.colour = DyeColor.valueOf((String) map.get("colour"));

		return (Z) this;
	}
}
