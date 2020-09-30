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

import org.bukkit.Art;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;

import com.palmergames.spigot.regen.serialize.SerializedEntity;
import com.palmergames.spigot.regen.serialize.SerializedObject;

/**
 * @author ElgarL
 *
 */
public class ExtendEntityPainting extends SerializedEntity {

	Art art = null;

	public ExtendEntityPainting () {}
	/**
	 * Constructor to create a ExtendEntityPainting from an Entity.
	 * 
	 * @param <T>	extends Entity
	 * @param block	the Entity to store.
	 */
	public <T extends Entity> ExtendEntityPainting(T entity) {

		super(entity);

		Painting painting = (Painting) entity;

		this.art = painting.getArt();

		/*
		 * Shift down half a block for Paintings with a Height of two.
		 * Prevents the Painting re-spawning one block too high.
		 */
		if (art.getBlockHeight() > 1)
			loc.add(0, -0.5, 0);

	}

	@Override
	public Entity regen() {

		Painting spawn = null;

		try {
			spawn = (Painting) super.regen();

			spawn.setArt(art, true);
			spawn.setFacingDirection(facing);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return spawn;
	}

	@Override
	public ExtendEntityPainting clone() {

		ExtendEntityPainting clone = null;

		clone = (ExtendEntityPainting) super.clone();
		clone.art = this.art;

		return clone;
	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> result = super.serialize();

		if (facing != null) result.put("facing", facing.name());
		if (art != null) result.put("art", art.name());


		return result;

	}

	@SuppressWarnings("unchecked")
	@Override
	public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

		super.deserialize(map);

		if (map.containsKey("art")) this.art = Art.valueOf((String) map.get("art"));

		return (Z) this;
	}
}
