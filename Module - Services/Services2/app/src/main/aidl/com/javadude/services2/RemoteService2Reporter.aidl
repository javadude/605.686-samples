package com.javadude.services2;
import com.javadude.services2.Person;

// An example callback interface that sends a list of people and a number back to the requester
interface RemoteService2Reporter {
	void report(in List<Person> people, in int n);
}