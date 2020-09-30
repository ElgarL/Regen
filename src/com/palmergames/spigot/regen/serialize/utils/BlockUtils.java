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
package com.palmergames.spigot.regen.serialize.utils;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import com.palmergames.spigot.regen.serialize.SerializedBlock;
import com.palmergames.spigot.regen.serialize.extensions.ExtendBlockBanner;
import com.palmergames.spigot.regen.serialize.extensions.ExtendBlockBeacon;
import com.palmergames.spigot.regen.serialize.extensions.ExtendBlockSign;

/**
 * @author ElgarL
 *
 */
public class BlockUtils {

	/**
	 * Serialize a Block based upon Material type.
	 * 
	 * @param block	the Block to serialize.
	 * @return		Map containing the serialised data.
	 */
	public static Map<String, Object> serializeByType (Block block) {

		Material type = block.getType();

		if (Tag.BANNERS.isTagged(type)) {

			return new ExtendBlockBanner(block).serialize();

		} else if (Tag.SIGNS.isTagged(type)) {

			return new ExtendBlockSign(block).serialize();

		} else {

			switch (type) {

			case BEACON:

				return new ExtendBlockBeacon(block).serialize();

			default:

				return new SerializedBlock(block).serialize();
			}
		}
	}

	/**
	 * Deserialize from a Map to a SerializedBlock based upon Material type.
	 * 
	 * @param <Z>	a class which extends SerializedBlock
	 * @param map	the map to process.
	 * @return		a SerializedBlock extended class.
	 * @throws IllegalArgumentException
	 */
	public static <Z extends SerializedBlock> Z deserializeByType (Map<?, ?> map) throws IllegalArgumentException {

		Material type = Bukkit.createBlockData((String)map.get("blockData")).getMaterial();

		if (Tag.BANNERS.isTagged(type)) {

			return new ExtendBlockBanner().deserialize(map);

		} else if (Tag.SIGNS.isTagged(type)) {

			return new ExtendBlockSign().deserialize(map);

		} else {

			switch (type) {

			case BEACON:

				return new ExtendBlockBeacon().deserialize(map);

			default:

				return new SerializedBlock().deserialize(map);
			}
		}
	}

}
