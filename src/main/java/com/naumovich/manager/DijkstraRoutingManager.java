package com.naumovich.manager;

import java.util.List;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.dijkstra.BackupMessage;
import com.naumovich.domain.message.dijkstra.ChunkMessage;
import com.naumovich.domain.message.dijkstra.DdsMessage;
import com.naumovich.network.MessageContainer;
import com.naumovich.table.AddressTable;
import com.naumovich.table.AddressTableEntry;
import com.naumovich.util.Dijkstra;
import com.naumovich.util.tuple.TwoTuple;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DijkstraRoutingManager implements RoutingManager {

	@Override
	public void distributeChunks(Node owner, AddressTable addressTable) {

	    for (AddressTableEntry entry : addressTable) {

	        List<Node> path = Dijkstra.findPathWithDijkstra(owner, entry.getNode());
			log.debug(owner.getLogin() + ": I send " + entry.getChunk().getChunkName() + " to " +
                    entry.getNode().getLogin() + ". The way is: " + path);

			if (path != null) {
				DdsMessage msg = new ChunkMessage(path, entry.getChunk());
				msg.excludeFirstNodeFromPath();
				MessageContainer.addMsg(msg);
			}

			// moreover, можно для результатов забабахть что-то вроде подсчета, как распределяются копии
			// например, на узел 5 попало две копии фрагмента 3
			// или вероятность того, что на один узел попадут все фрагменты файла
			// (без копий, просто, например, все 8 штук)... было бы интересно
		}
	}

	@Override
	public void checkNodesStatus(Node owner, AddressTable addressTable) {
		/*for (int i = 0; i < addressTable.getRowCount(); i++) {
			AddressTableEntry entry = addressTable.getRow(i);
			owner.incrementAmountOfNodeStatusChecks();
			if (!entry.getNode().isOnline()) {
				log.debug(owner.getLogin() + ": I've found out " + entry.getNode() + " is offline");
				TwoTuple<Node, Integer> nodeAndMetrics = entry.getChunk().findNodeForMe();
				if (entry.getNode().equals(nodeAndMetrics.first)) {
					break; // same node returned so no more need for backup
				}
				else {
					addressTable.setRow(i, nodeAndMetrics.first, nodeAndMetrics.second);
					int rowOfSender = getNewSender(addressTable, i);
					Node sender = addressTable.getRow(rowOfSender).getNode();
					Chunk chunkToSend = addressTable.getRow(rowOfSender).getChunk();
					log.debug(owner.getLogin() + ": new sender of " + chunkToSend + " is " + sender);
					List<Node> path = Dijkstra.findPathWithDijkstra(owner, sender);
					if (path != null) {
						DdsMessage backupMsg = new BackupMessage(path, new TwoTuple<>(nodeAndMetrics.first, chunkToSend) );
						backupMsg.excludeFirstNodeFromPath();
						MessageContainer.addMsg(backupMsg);
					}
				}

			}
		}*/
	}

	private int getNewSender(AddressTable addressTable, int i) {
		int orderNum = addressTable.getRow(i).getOrderNum();
		for (int j = 0; j < addressTable.getRowCount(); j++) {
			AddressTableEntry entry = addressTable.getRow(j);
			if (entry.getOrderNum() == orderNum && !entry.getNode().equals(addressTable.getRow(i).getNode())) {
				return j;
			}
		}
		return 0;
	}
}
