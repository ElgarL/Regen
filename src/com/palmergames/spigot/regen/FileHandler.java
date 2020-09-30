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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import com.palmergames.spigot.regen.serialize.SerializedBlock;
import com.palmergames.spigot.regen.serialize.utils.BlockUtils;
import com.palmergames.spigot.regen.serialize.utils.EntityUtils;

/**
 * Handles saving, loading, decoding
 * and processing of data in preparation
 * of regeneration.
 * 
 * @author ElgarL
 *
 */
public class FileHandler {

	private File blockSourceFolder;
	private File entitySourceFolder;
	private Regen plugin;

	private FileConfiguration config;

	/**
	 * 
	 */
	public FileHandler(Regen plugin) {

		this.plugin = plugin;

		blockSourceFolder = new File(plugin.getDataFolder() + File.separator + "blocks");
		if (!blockSourceFolder.exists())
			blockSourceFolder.mkdirs();

		entitySourceFolder = new File(plugin.getDataFolder() + File.separator + "entities");
		if (!entitySourceFolder.exists())
			entitySourceFolder.mkdirs();
	}

	public void loadConfig() {

		plugin.saveDefaultConfig();
		/*
		 * Ensure we load the config clean in case of a reload.
		 */
		plugin.reloadConfig();
		config = plugin.getConfig();
	}

	public Long getDelay() {

		return config.getLong("delay", 10L);
	}

	public Long getFrequency() {

		return config.getLong("frequency", 5L);
	}

	/**
	 * Attempt to serialize an Entity and save to file.
	 * Pass the serialized data to be regenerated.
	 * 
	 * @param <T>		extends Entity
	 * @param entity	the Entity to process.
	 */
	public <T extends Entity> void saveEntity(final T entity) {

		YamlConfiguration data = new YamlConfiguration();
		String uid = UUID.randomUUID().toString();

		data.createSection(uid, EntityUtils.serializeByType(entity));

		File file = new File(entitySourceFolder, uid + ".yml");
		saveData(data, file);

		// Schedule a regen.

		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> { decodeEntityData(data); }, getDelay() * 20);
	}

	/**
	 * Attempt to serialize a List of Blocks and save to file.
	 * Pass the serialized data to be regenerated.
	 * 
	 * @param <T>		extends Block
	 * @param blocks	the Blocks to process.
	 */
	public <T extends Block> void saveBlocks(final List<T> blocks) {

		if (blocks == null || blocks.isEmpty()) return;

		YamlConfiguration data = new YamlConfiguration(); 
		List<Map<String, Object>> explosion = new ArrayList<Map<String, Object>>();

		blocks.forEach(block -> { explosion.add(BlockUtils.serializeByType(block)); });

		String uid = UUID.randomUUID().toString();
		data.set(uid, explosion);


		File file = new File(blockSourceFolder, uid + ".yml");
		saveData(data, file);

		// Schedule a regen.

		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> { decodeBlockData(data); }, getDelay() * 20);
	}

	/**
	 * Load all entities from files
	 * and begin deserializing.
	 */
	public void loadEntities() {

		synchronized(fileLock) {
			for (String name : entitySourceFolder.list()) {

				File file = new File(entitySourceFolder, name);

				if (!file.exists() || (file.length() == 0)) return;

				YamlConfiguration data = new YamlConfiguration();

				try {
					data.load(file);
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}

				// Decode the data

				decodeEntityData(data);
			}
		}
	}

	/**
	 * Load all blocks from files
	 * and begin deserializing.
	 */
	public void loadBlocks() {

		synchronized(fileLock) {
			for (String name : blockSourceFolder.list()) {

				File file = new File(blockSourceFolder, name);

				if (!file.exists() || (file.length() == 0)) continue;

				YamlConfiguration data = new YamlConfiguration();

				try {
					data.load(file);
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}

				// Decode the data

				decodeBlockData(data);
			}
		}
	}

	private Object decodeLock = new Object();

	/**
	 * Deserialize data back into SerializedBlock Objects
	 * and pass for regeneration.
	 * 
	 * @param data
	 */
	private void decodeBlockData(YamlConfiguration data) {

		synchronized(decodeLock) {
			try {
				for (String key : data.getKeys(false)) {

					List<? extends SerializedBlock> blocks = new ArrayList<>();

					List<Map<?, ?>> mapList = data.getMapList(key);

					for (Map<?, ?> map : mapList) {

						blocks.add(BlockUtils.deserializeByType(map));
					}

					if (!blocks.isEmpty()) plugin.getTaskHolder().addBlocks(key ,blocks);
				}

			} catch (Exception e) {
				System.out.println("Failed Decoding Blocks to respawn.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Deserialize data back into a SerializedEntity Object
	 * and pass for regeneration.
	 * 
	 * @param data
	 */
	private void decodeEntityData(YamlConfiguration data) {

		synchronized(decodeLock) {
			try {
				for (String key : data.getKeys(false)) {

					Map<String, Object> map = data.getConfigurationSection(key).getValues(true);

					plugin.getTaskHolder().addEntity(key, EntityUtils.deserializeByType(map));
				}

			} catch (Exception e) {
				System.out.println("Failed Decoding Entities to respawn.");
				e.printStackTrace();
			}
		}
	}

	private Object fileLock = new Object();

	private void saveData(FileConfiguration data, File file) {

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			synchronized(fileLock) {
				try {
					data.save(file);
				} catch (IOException e) {
					System.err.println("Saving data error.");
					e.printStackTrace();
				}
			}
		});
	}

	protected void deleteBlockData(String uid) {

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			synchronized(fileLock) {
				File file = new File(blockSourceFolder, uid + ".yml");

				if (!file.exists()) return;

				file.delete();
			}
		});
	}

	protected void deleteEntityData(String uid) {

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			synchronized(fileLock) {
				File file = new File(entitySourceFolder, uid + ".yml");

				if (!file.exists()) return;

				file.delete();
			}
		});
	}
}
