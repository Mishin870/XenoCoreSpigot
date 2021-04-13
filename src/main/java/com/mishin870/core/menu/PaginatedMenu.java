package com.mishin870.core.menu;

import com.mishin870.core.XenoCore;
import com.mishin870.core.utils.InventoryUtils;
import com.mishin870.core.utils.Presentable;
import com.mishin870.core.utils.rotator.Rotator;
import com.mishin870.core.utils.rotator.SimpleRotator;
import com.mishin870.core.utils.actions.ActionTwo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class PaginatedMenu extends Menu {
	private final int height;
	private final List<ItemStack> items = new ArrayList<>();
	private final List<MenuCommand> actions = new ArrayList<>();
	private final String leftTitle;
	private final String rightTitle;
	private final int perPage;
	
	private int maxPage = 0;
	private int page = 0;
	
	public static Rotator<Material> defaultRotator() {
		return new SimpleRotator<>(Material.GRASS_BLOCK, Material.PODZOL, Material.SNOW_BLOCK, Material.GRASS_PATH);
	}
	
	public PaginatedMenu(Player player, String title) {
		this(player, title, 3);
	}
	
	public PaginatedMenu(Player player, String title, int height) {
		super(title, height);
		
		this.height = height;
		this.perPage = LINE_LENGTH * (height - 1);
		
		this.leftTitle = XenoCore.language.get(player, "menu.left");
		this.rightTitle = XenoCore.language.get(player, "menu.right");
		
		reCalculate();
		updatePage();
	}
	
	public <T extends Presentable> void addItems(Player player, Iterable<T> items, ActionTwo<Menu, T> action) {
		addItems(player, defaultRotator(), items, action);
	}
	
	public <T extends Presentable> void addItems(Player player, Rotator<Material> rotator, Iterable<T> items,
	                                             ActionTwo<Menu, T> action) {
		for (final var item : items) {
			addItem(item.getPresentation(player, rotator.next()), menu -> action.run(menu, item));
		}
	}
	
	public void addItem(Rotator<Material> rotator, String title, MenuCommand action) {
		addItem(rotator.next(), title, action);
	}
	
	public void addItem(Material material, String title, MenuCommand action) {
		addItem(InventoryUtils.createTitled(material, title), action);
	}
	
	public void addItem(ItemStack item, MenuCommand action) {
		items.add(item);
		actions.add(action);
		reCalculate();
	}
	
	public void setSpecial(int x, Material material, String title, MenuCommand command) {
		setCommand(getSlot(x, height - 1), material, title, command);
	}
	
	private void reCalculate() {
		this.maxPage = items.size() / perPage;
	}
	
	public void updatePage() {
		final var offset = page * perPage;
		
		for (var i = 0; i < perPage; i++) {
			final var index = offset + i;
			final var item = index < items.size() ? items.get(index) : null;
			
			if (item != null) {
				setCommand(i, item, actions.get(index));
			} else {
				setCommand(i, null, null);
			}
		}
		
		setPaginationButtons(page > 0, page < maxPage);
	}
	
	private void doLeft(Menu menu) {
		page--;
		
		if (page <= 0) {
			page = 0;
		}
		
		updatePage();
	}
	
	private void doRight(Menu menu) {
		page++;
		
		if (page > maxPage) {
			page = maxPage;
		}
		
		updatePage();
	}
	
	private void setPaginationButtons(boolean isLeft, boolean isRight) {
		if (isLeft) {
			setCommand(3, height - 1, Material.SOUL_LANTERN, leftTitle, this::doLeft);
		} else {
			setCommand(3, height - 1, null, null);
		}
		
		if (isRight) {
			setCommand(5, height - 1, Material.LANTERN, rightTitle, this::doRight);
		} else {
			setCommand(5, height - 1, null, null);
		}
	}
}
