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

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired before a group of Blocks are destroyed due to an explosion.
 * 
 * Remove blocks from this event to prevent them being damaged.
 * Any remaining blocks will not drop items and will be regenerated.
 * 
 * If we cancel this event the explosion will still happen and blocks
 * will drop as items; however, no regeneration will occur.
 * 
 * @author ElgarL
 *
 */
public class BlockRegenEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel;
	private final List<Block> blockList;

	/**
	 * 
	 */
	public BlockRegenEvent(List<Block> blockList) {

		this.blockList = blockList;
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
	 * Returns the list of blocks that
	 * will be or have been removed
	 * by an explosion event.
	 *
	 * @return All blown-up blocks
	 */
	public List<Block> blockList() {
		return this.blockList;
	}

	@Override
	public HandlerList getHandlers() {

		return handlers;
	}

	public static HandlerList getHandlerList() {

		return handlers;
	}

}
