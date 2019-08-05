/prompt the user for a name & a grade or '.' to quit;
		System.out.print("Enter a name followed by a grade "
						+ "(or . to quit): ");
		userInput = console.next();

		//Set the next empty entry in the name array to the given name
		int nextName = 0;
		name[nextName] = " ";
		nextName++;

		/*Set the next empty entry in the grade array to the given
		grade */
		int nextEntry = 0;
		grade[nextEntry]++;

		/*if the user's entry begins with something other than a letter
		* or if you've run out of empty entries in the array, return
		* the number of entries you populated in the array
		*/

		do {
			
			if (Character.isLetter(userInput.charAt(0))) {
			System.out.println(userInput);
			} else {
				System.out.println();
		} 
		while (!done);
	}