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
package com.palmergames.spigot.regen.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * If this event is cancelled the entity that was destroyed will not be regenerated.
 * It will however drop any block and inventory it normally would.
 * 
 * @author ElgarL
 *
 */
public class EntityRegenEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel;
	private final Entity entity;

	/**
	 * Constructor for an Entity regen event.
	 * 
	 * @param entity	the Entity that was destroyed.
	 */
	public EntityRegenEvent(Entity entity) {

		this.entity = entity;
		this.cancel = false;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	/**
	 * Returns the Entity that
	 * will be or has been removed
	 * by an explosion event.
	 *
	 * @return blown-up Entity
	 */
	public Entity entity() {
		return this.entity;
	}

	@Override
	public HandlerList getHandlers() {

		return handlers;
	}

	public static HandlerList getHandlerList() {

		return handlers;
	}

}

