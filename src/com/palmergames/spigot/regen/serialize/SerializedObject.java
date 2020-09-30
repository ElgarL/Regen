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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

/**
 * @author ElgarL
 *
 */
public abstract class SerializedObject implements Cloneable {

	protected List<ItemStack> items = new ArrayList<ItemStack>();
	protected Location loc = null;

	public SerializedObject () {}

	/**
	 * Attempt to place the block or Entity
	 * this represents back into the world.
	 * 
	 * @return	the Entity or BlockState this
	 * 			created, or null if it failed
	 */
	public abstract Object regen();

	@Override
	public SerializedObject clone() {

		SerializedObject clone = null;
		try {
			clone = (SerializedObject) super.clone();
			clone.loc = this.loc.clone();
			clone.items.addAll(this.items);

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}

	/**
	 * Convert this Object to a {@literal Map<String, Object>} in
	 * Preparation for saving or processing back into
	 * an identical Object
	 * 
	 * @return	a {@literal Map<String, Object>}
	 * 			containing the serialised data of this Object.
	 */
	public Map<String, Object> serialize() {

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("location", loc.serialize());

		if (hasInventory()) {
			List<Map<String, Object>> inventory = new ArrayList<Map<String, Object>>();

			/*
			 * Add individually to eliminate
			 * the class entry in the final map.
			 */
			for (ItemStack itemStack : this.items) {
				inventory.add(itemStack.serialize());
			}
			result.put("inventory", inventory);
		}

		return result;
	}

	/**
	 * Converts a serialised Map representation
	 * of this class back into a usable Object.
	 * 
	 * @param <Z>	extends SerializedObject.
	 * @param map	a {@literal Map<?, ?>} to convert.
	 * @return		a deserialized SerializedObject.
	 * @throws IllegalArgumentException	if the World doesn't exist.
	 */
	@SuppressWarnings("unchecked")
	public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

		this.loc = Location.deserialize(castToMap(map.get("location")));

		if (map.containsKey("inventory")) {

			List<Map<String, Object>> inventory = (List<Map<String, Object>>) map.get("inventory");

			for (Map<String, Object> item : inventory) {
				ItemStack stack = ItemStack.deserialize(item);
				this.items.add(stack);
			}
		}

		return (Z) this;
	}

	/**
	 * @return true if this Object has an Inventory
	 */
	public Boolean hasInventory() {

		return this.items != null && !this.items.isEmpty();
	}

	/**
	 * @return	an array of the Inventory contents.
	 */
	public ItemStack[] getInventory() {

		return this.items.toArray(new ItemStack[this.items.size()]);
	}

	/**
	 * @return	the Location for this Object.
	 */
	public Location getLocation() {

		return this.loc;
	}

	/**
	 * @param location	to set for this Object
	 */
	public void setLocation(Location location) {

		this.loc = location;
	}

	/**
	 * If loading from file the Maps in the data
	 * will be stored as a MemorySection not Maps.
	 * 
	 * @param entry	MemorySection or Map to check.
	 * @return		Map containing the serialised data.
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> castToMap(Object entry) {

		if (entry instanceof MemorySection) {
			return ((MemorySection) entry).getValues(true);
		} else {
			return (Map<String, Object>) entry;
		}
	}
}
