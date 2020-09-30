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

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.palmergames.spigot.regen.serialize.SerializedEntity;
import com.palmergames.spigot.regen.serialize.extensions.ExtendEntityArmorStand;
import com.palmergames.spigot.regen.serialize.extensions.ExtendEntityItemFrame;
import com.palmergames.spigot.regen.serialize.extensions.ExtendEntityPainting;

/**
 * @author ElgarL
 *
 */
public class EntityUtils {

	/**
	 * Deserialize an Entity based upon EntityType.
	 * 
	 * @param entity	the Entity to serialize.
	 * @return			Map containing the serialised data.
	 */
	public static Map<String, Object> serializeByType (Entity entity) {

		switch (entity.getType()) {

		case ARMOR_STAND:

			return new ExtendEntityArmorStand(entity).serialize();

		case ITEM_FRAME:

			return new ExtendEntityItemFrame(entity).serialize();

		case PAINTING:

			return new ExtendEntityPainting(entity).serialize();

		default:

			return new SerializedEntity(entity).serialize();
		}
	}

	/**
	 * Deserialize from a Map to a SerializedEntity based upon EntityType.
	 * 
	 * @param <Z>	a class which extends SerializedEntity
	 * @param map	the map to process.
	 * @return		a SerializedEntity extended class.
	 * @throws IllegalArgumentException
	 */
	public static <Z extends SerializedEntity> Z deserializeByType (Map<?, ?> map) throws IllegalArgumentException {

		EntityType type = EntityType.valueOf((String) map.get("type"));

		switch (type) {

		case ARMOR_STAND:

			return new ExtendEntityArmorStand().deserialize(map);

		case ITEM_FRAME:

			return new ExtendEntityItemFrame().deserialize(map);

		case PAINTING:

			return new ExtendEntityPainting().deserialize(map);

		default:

			return new SerializedEntity().deserialize(map);
		}
	}

}
