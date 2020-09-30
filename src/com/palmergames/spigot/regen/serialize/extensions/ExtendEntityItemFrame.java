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

import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import com.palmergames.spigot.regen.serialize.SerializedEntity;
import com.palmergames.spigot.regen.serialize.SerializedObject;

/**
 * @author ElgarL
 *
 */
public class ExtendEntityItemFrame extends SerializedEntity {

	Rotation rotation = null;

	public ExtendEntityItemFrame () {}
	/**
	 * Constructor to create a ExtendEntityItemFrame from an Entity.
	 * 
	 * @param <T>	extends Entity
	 * @param block	the Entity to store.
	 */
	public <T extends Entity> ExtendEntityItemFrame(T entity) {

		super(entity);

		ItemFrame frame = (ItemFrame)entity;

		rotation = frame.getRotation();
		customName = frame.getCustomName();

		this.items.add(frame.getItem());
	}

	@Override
	public Entity regen() {

		ItemFrame spawn = null;

		try {
			spawn = (ItemFrame) super.regen();

			spawn.setFacingDirection(facing, true);
			spawn.setRotation(rotation);

			if (customName != null) spawn.setCustomName(customName);

			if (hasInventory()) spawn.setItem(getInventory()[0]);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return spawn;
	}

	@Override
	public ExtendEntityItemFrame clone() {

		ExtendEntityItemFrame clone = null;

		clone = (ExtendEntityItemFrame) super.clone();
		clone.rotation = this.rotation;

		return clone;
	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> result = super.serialize();

		if (rotation != null) result.put("rotation", rotation.name());
		return result;

	}

	@SuppressWarnings("unchecked")
	@Override
	public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

		super.deserialize(map);

		if (map.containsKey("rotation")) this.rotation = Rotation.valueOf((String) map.get("rotation"));

		return (Z) this;
	}
}
