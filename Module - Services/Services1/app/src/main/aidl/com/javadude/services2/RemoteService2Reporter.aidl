package com.javadude.services2;
import com.javadude.services2.Person;

interface RemoteService2Reporter {
	void report(in List<Person> people, in int n);
}