package com.javadude.services2;
import com.javadude.services2.RemoteService2Reporter;

interface RemoteService2 {
	void reset();
	void add(RemoteService2Reporter reporter);
	void remove(RemoteService2Reporter reporter);
}