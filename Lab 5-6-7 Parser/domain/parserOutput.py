class ParserOutput:
    def __init__(self, parser):
        # reference of the parser
        self.parser = parser

    def write_parsing_tree(self):
        # prints the parsing tree to the file
        '''
        The function checks to see if the parser state is not equal to "e", meaning that the parser state is not in an error state.
        The function then prints the header of the parsing tree to the file.
        The function then iterates through the working stack of the parser.
        For each iteration, the function creates a message string which contains the index and the tree info of the current index.
        The function then writes the message string to the output file.
        '''
        if self.parser.state != "e":
            self.parser.write_out("\nParsing tree: ")
            self.parser.write_out("index info parent  left_sibling")
            for index in range(0, len(self.parser.stack)):
                message = str(index) + "  " + str(self.parser.tree[index])
                self.parser.write_out(message)