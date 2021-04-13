package com.mishin870.core.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Menu {
	private static final List<Menu> menus = new ArrayList<>();
	protected static final int LINE_LENGTH = 9;
	
	public final String title;
	private final Inventory inventory;
	private final MenuCommand[] commands;
	
	static void init() {
		menus.clear();
	}
	
	static Menu getByInventory(Inventory inventory) {
		for (Menu menu : menus) {
			if (menu.inventory.equals(inventory)) return menu;
		}
		
		return null;
	}
	
	public Menu(String title) {
		this(title, 3);
	}
	
	public Menu(String title, int height) {
		this.title = title;
		this.inventory = Bukkit.createInventory(null, height * LINE_LENGTH, title);
		this.commands = new MenuCommand[this.inventory.getSize()];
		
		menus.add(this);
	}
	
	public void open(Player player) {
		player.openInventory(inventory);
	}
	
	protected int getSlot(int x, int y) {
		return y * LINE_LENGTH + x;
	}
	
	public void setCommand(int x, int y, Material material, String title, MenuCommand command) {
		setCommand(getSlot(x, y), material, title, command);
	}
	
	public void setCommand(int slot, Material material, String title, MenuCommand command) {
		final var item = new ItemStack(material);
		final var meta = item.getItemMeta();
		Objects.requireNonNull(meta).setDisplayName(title);
		item.setItemMeta(meta);
		
		setCommand(slot, item, command);
	}
	
	public void setCommand(int x, int y, ItemStack stack, MenuCommand command) {
		setCommand(getSlot(x, y), stack, command);
	}
	
	public void setCommand(int slot, ItemStack stack, MenuCommand command) {
		inventory.setItem(slot, stack);
		commands[slot] = command;
	}
	
	public void onClick(int slot) {
		if (slot < 0 || slot >= commands.length) return;
		final var command = commands[slot];
		if (command == null) return;
		
		command.run(this);
	}
	
	public final void closeEvent(InventoryCloseEvent closeEvent) {
		onClose(closeEvent);
		menus.remove(this);
	}
	
	public void onClose(InventoryCloseEvent event) {
	}
}
