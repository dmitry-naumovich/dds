package com.naumovich.manager;

import java.util.List;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.ChunkMessage;
import com.naumovich.domain.message.Message;
import com.naumovich.domain.message.ResCopyMessage;
import com.naumovich.network.MessageContainer;
import com.naumovich.table.AddressTable;
import com.naumovich.util.Dijkstra;
import com.naumovich.util.tuple.FourTuple;
import com.naumovich.util.tuple.TwoTuple;

public class DijkstraRoutingManager implements RoutingManager {

	
	@Override
	public void distributeChunks(Node owner, AddressTable addressTable) {
		for (FourTuple<Integer, Chunk, Node, Integer> tableRow : addressTable) {
			Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
			dijAlg.execute(owner); // Dijkstra works
			List<Node> path = dijAlg.getPath(tableRow.third);
			owner.incrementAmountOfFindingPath();
			System.out.println(owner.getLogin() + ": I send " + tableRow.second.getChunkName() + " to " +
					tableRow.third.getLogin() + ". The way is: " + path);
			if (path != null) {
				Message msg = new ChunkMessage(path, tableRow.second);
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
		for (int i = 0; i < addressTable.getRowCount(); i++) {
			FourTuple<Integer, Chunk, Node, Integer> tup = addressTable.getRow(i);
			owner.incrementAmountOfNodeStatusChecks();
			if (!tup.third.isOnline()) {
				System.out.println(owner.getLogin() + ": I've found out " + tup.third + " is offline");
				TwoTuple<Node, Integer> tup2 = tup.second.findNodeForMe();
				if (tup.third.equals(tup2.first)) {
					break; // same node returned so no more need for reserve copying
				}
				else {
					addressTable.setRow(i, tup2.first, tup2.second);
					int rowOfSender = getNewSender(addressTable, i);
					Node sender = addressTable.getRow(rowOfSender).third;
					Chunk chunkToSend = addressTable.getRow(rowOfSender).second;
					System.out.println(owner.getLogin() + ": new sender of " + chunkToSend + " is " + sender);
					Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
					dijAlg.execute(owner);
					List<Node> path = dijAlg.getPath(sender);
					owner.incrementAmountOfFindingPath();
					if (path != null) {
						Message resCopyMsg = new ResCopyMessage(path, new TwoTuple<>(tup2.first, chunkToSend) );
						resCopyMsg.excludeFirstNodeFromPath();
						MessageContainer.addMsg(resCopyMsg);
					}
				}

			}
		}
	}

	private int getNewSender(AddressTable addressTable, int i) {
		int orderNum = addressTable.getRow(i).first;
		for (int j = 0; j < addressTable.getRowCount(); j++) {
			FourTuple<Integer, Chunk, Node, Integer> t = addressTable.getRow(j);
			if (t.first == orderNum && !t.third.equals(addressTable.getRow(i).third)) {
				return j;
			}
		}
		return 0;
	}
}
