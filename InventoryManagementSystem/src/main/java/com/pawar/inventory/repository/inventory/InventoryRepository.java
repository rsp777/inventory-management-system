package com.pawar.inventory.repository.inventory;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.hibernate.Session;

import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;


public interface InventoryRepository {

	public Inventory createInventory(Lpn lpn,Session currentSession);
	public String createReserveInventory(Lpn lpn,Location location) throws ParseException, IOException;
	public String createActiveInventory(Lpn lpn,Location location) throws ClientProtocolException, IOException;
	public Iterable<Inventory> viewAllInventory();
	public Inventory getInventoryByLpn(String lpn_name);
	public List<Inventory> getInventorybyItem(Item item);
	public void deleteByInventoryLpn(String lpn_name);
	public List<Inventory> getInventoryByLocation(Location location);
	public Inventory updateInventoryQty(Inventory inventory,int adjustQty);
	public void updateInventory(Lpn lpn);	
}
