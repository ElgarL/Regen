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

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.spigot.regen.metrics.Metrics;

/**
 * @author ElgarL
 *
 */
public class Regen extends JavaPlugin {

	private Regen instance;
	private String version;

	private FileHandler fileHandler;
	private TaskHolder taskHolder;
	private Detection detection;

	/**
	 * 
	 */
	public Regen() {

		// Store our instance.
		instance = this;
		/*
		 * Initialize everything we can here
		 * but do NOTHING that attempts to load
		 * any data, nor access any other plug-ins.
		 */
		version = instance.getDescription().getVersion();
		fileHandler = new FileHandler(instance);
		taskHolder = new TaskHolder(instance);
		detection = new Detection(instance);
	}

	@Override
	public void onEnable() {

		/*
		 * Executes after the worlds have initialized.
		 * 
		 * Hook other plug-ins, register commands and
		 * perform any remaining tasks to be fully
		 * up and running.
		 */
		final PluginManager pluginManager = getServer().getPluginManager();

		pluginManager.registerEvents(detection, instance);

		fileHandler.loadConfig();
		fileHandler.loadBlocks();
		fileHandler.loadEntities();
		
		/*
		 * Register Metrics
		 */
		try {
			new Metrics(this, 9000);
		    
		} catch (Exception e) {
			System.err.println(getFormattedName() + "Error setting up metrics");
		}
	}

	@Override
	public void onDisable() {

		taskHolder.cancelTask();
	}

	/**
	 * Handles saving, loading, decoding
	 * and processing of data in preparation
	 * of regeneration.
	 * 
	 * @return	the FileHandler
	 */
	public FileHandler getFileHandler(){

		return fileHandler;
	}

	/**
	 * Holds the Queues for regeneration and the
	 * Asynchronous Task used for processing.
	 * 
	 * @return	the TaskHolder
	 */
	protected TaskHolder getTaskHolder(){

		return taskHolder;
	}

	/**
	 * Detects all explosions and process for
	 * regeneration of destroyed entities and
	 * Blocks.
	 * 
	 * @return	the Detection Listener
	 */
	public Detection getDetection(){

		return detection;
	}

	public String getVersion() {

		return version;
	}

	protected String getFormattedName() {

		return "[" + getName() + "] ";
	}
}
