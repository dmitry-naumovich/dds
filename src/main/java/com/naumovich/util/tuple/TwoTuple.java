package com.naumovich.util.tuple;

//TODO: replace all tuples with the tuple analogue from java libs (look to dasreda proj)
public class TwoTuple<A, B> {
	
	public final A first;
	public final B second;
	
	public TwoTuple(A a, B b) {
		first = a; 
		second = b;
	}

	@Override
	public String toString() {
		return "TwoTuple [first=" + first + ", second=" + second + "]";
	}
	
}
