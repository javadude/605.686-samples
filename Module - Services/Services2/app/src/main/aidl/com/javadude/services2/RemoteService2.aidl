package com.javadude.services2;
import com.javadude.services2.RemoteService2Reporter;

// AIDL that defines our API with the remote service
// Here we allow a "reset" request as well as adding/removing a callback
interface RemoteService2 {
	void reset();
	void add(RemoteService2Reporter reporter);
	void remove(RemoteService2Reporter reporter);
}