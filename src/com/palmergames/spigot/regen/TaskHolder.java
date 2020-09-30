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
package com.palmergames.spigot.regen;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.palmergames.spigot.regen.serialize.SerializedBlock;
import com.palmergames.spigot.regen.serialize.SerializedObject;

/**
 * Holds the Queues for regeneration
 * and the Async Task used for processing.
 * 
 * @author ElgarL
 *
 */
public class TaskHolder {

	private Queue<Entry<String, List<SerializedBlock>>> blockQueue = new ConcurrentLinkedQueue<Entry<String, List<SerializedBlock>>>();
	private Queue<Entry<String, SerializedObject>> entityQueue = new ConcurrentLinkedQueue<Entry<String, SerializedObject>>();

	private BukkitTask task = null;

	private Regen plugin;
	private FileHandler handler;

	/**
	 * 
	 */
	public TaskHolder(Regen plugin) {

		this.plugin = plugin;
		handler = this.plugin.getFileHandler();
	}

	/**
	 * Add blocks to the queue for regeneration.
	 * 
	 * @param key		A Unique ID to store this regeneration group under.
	 * @param blocks	A collection of blocks to regenerate.
	 */
	public void addBlocks(String key, Collection<? extends SerializedBlock> blocks) {

		List<SerializedBlock> regen = new ArrayList<SerializedBlock>();

		regen.addAll(blocks);

		Collections.sort(regen, new Comparator<SerializedBlock>() {

			@Override
			public int compare(SerializedBlock block1, SerializedBlock block2) {

				return block1.getLocation().getBlockY() - block2.getLocation().getBlockY();
			}

		});

		if (regen.size() > 0) {

			Entry<String, List<SerializedBlock>> entry = new AbstractMap.SimpleEntry<String, List<SerializedBlock>>(key, regen);

			blockQueue.add(entry);

			if (task == null)
				startMonitor();
		}
	}

	/**
	 * Add this entity to the queue to be regenerated.
	 * 
	 * @param key		A Unique ID to store this regeneration.
	 * @param entity	An entity to re-spawn.
	 */
	public void addEntity(String key, SerializedObject entity) {

		if (entity != null) {

			Entry<String, SerializedObject> entry = new AbstractMap.SimpleEntry<String, SerializedObject>(key, entity);

			entityQueue.add(entry);

			if (task == null)
				startMonitor();
		}
	}

	private void startMonitor() {

		task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

			public void run() {

				if (!blockQueue.isEmpty()) {

					int size = blockQueue.size();
					Entry<String, List<SerializedBlock>> entry;
					/*
					 * Pull every entry and regen one
					 * block from each list per pass.
					 */
					for (int i = 0; i < size; i++) {
						entry = blockQueue.poll();

						List<SerializedBlock> blocks = entry.getValue();
						SerializedObject block = blocks.remove(0);
						Bukkit.getScheduler().runTask(plugin, () -> block.regen());

						if (blocks.size() > 0) {
							/*
							 * Add the entry back onto the
							 * end of the queue as there
							 * are more blocks to regen.
							 */
							entry.setValue(blocks);
							blockQueue.add(entry);

						} else {
							//System.out.println("Finished: " + entry.getKey());
							/*
							 * Delete any data files for this entry
							 * as we have finished its regen.
							 */
							handler.deleteBlockData(entry.getKey());
						}
					}
				} else if (!entityQueue.isEmpty()) {
					/*
					 * Regen entities one at a time.
					 */
					Entry<String, SerializedObject> entry = entityQueue.poll();

					Bukkit.getScheduler().runTask(plugin, () -> entry.getValue().regen());
					/*
					 * Delete any data files for this entry
					 * as we have finished its regen.
					 */
					handler.deleteEntityData(entry.getKey());
				}
			}

		}, handler.getDelay() * 20, handler.getFrequency());
	}

	/**
	 * Call from onDisable() to stop the Asynchronous Task.
	 */
	public void cancelTask() {

		if (task != null) task.cancel();
	}

}
