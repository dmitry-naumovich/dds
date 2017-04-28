package com.naumovich.manager;

import com.naumovich.domain.Node;
import com.naumovich.table.AddressTable;

public interface RoutingManager {

	void distributeChunks(Node owner, AddressTable addressTable);
	void checkNodesStatus(Node owner, AddressTable addressTable);

}
