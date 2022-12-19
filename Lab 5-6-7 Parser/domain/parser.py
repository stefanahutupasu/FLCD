from domain.grammar import Grammar
from domain.node import Node
from domain.parserOutput import ParserOutput


class Parser:
    def __init__(self, grammar_file, seq_file, out_file):
        self.grammar = Grammar.from_file(grammar_file)
        self.output_file = out_file
        self.stack = []
        self.input_stack = [self.grammar.getS()[0]]
        self.sequence = self.read_sequence(seq_file)

        # q - normal state, b - back state, f - final state, e - error state
        self.state = "q"
        self.current_position = 0
        self.tree = []

        self.parserOutput = ParserOutput(self)

    # reads the input sequence from the given file
    def read_sequence(self, sequence_file):
        sequence = []
        with open(sequence_file) as file:
            if sequence_file == "PIF.out":
                line = file.readline()
                line = file.readline()
                while line:
                    elems_line = line.split(" ")
                    sequence.append(elems_line[0])
                    line = file.readline()
            else:
                line = file.readline()
                while line:
                    sequence.append(line[0:-1])
                    line = file.readline()
        return sequence

    def write_out(self, message, final=False):
        with open(self.output_file, 'a') as file:
            if final:
                file.write("-------RESULT:-------\n")
            file.write(message + "\n")
        file.close()

    def write_all_data(self):
        with open(self.output_file, 'a') as file:
            # f.write("--------------\n")
            file.write("State: " + str(self.state) + "\n")
            file.write("Current position: " + str(self.current_position) + "\n")
            file.write("Working stack: " + str(self.stack) + "\n")
            file.write("Input stack: " + str(self.input_stack) + "\n")

    def expand(self):
        # used when head of input stack is a nonterm
        # (q, i, alpha, A beta) ⊢ (q, i, alpha A1, gamma1 beta)
        '''This method is used to expand the input stack when the head of the stack is a non-terminal. I
        it removes the non-terminal from the head of the stack, adds it to the working stack,
        and adds the new production to the input stack. '''
        print("---expand---")
        self.write_out("---expand---")
        nonterm = self.input_stack.pop(0)  # pop A from beta
        self.stack.append((nonterm, 0))  # alpha -> alpha A1
        new_production = [self.grammar.get_productions_for_nonterm(nonterm)[0][0]]
        print(new_production)
        print(self.input_stack)
        self.input_stack = new_production + self.input_stack

    def advance(self):
        # used when head of input stack is a terminal = current symbol from input
        # (q, i, alpha, a_i beta) ⊢ (q, i+1, alpha a_i, beta)
        '''
        This method is used to advance the input stack when the head of the stack is a terminal
        that matches the current symbol of the input.
        It removes the terminal from the head of the stack and adds it to the working stack, and increases the index of the input.
        :return:
        '''
        print("---advance---")
        self.write_out("---advance---")
        self.stack.append(self.input_stack.pop(0))
        self.current_position += 1
        print(self.stack)

    def momentary_insuccess(self):
        # used when head of input stack is a terminal != current symbol from input
        '''
         Function to handle a momentary insuccess in the working stack.
        The head of the input stack will be a terminal that does not match the current symbol from the input.
        The state will be set to 'b'.
        :return:
        '''
        print("---momentary insuccess---")
        self.write_out("---momentary insuccess---")
        self.state = "b"

    def back(self):
        '''The back() method pops the head of the working stack and adds it to the input stack
        while decreasing the index variable by 1.
        used when head of working stack is a terminal '''
        # (b, i, alpha a, beta) ⊢ (b, i-1, alpha, a beta)
        print("---back---")
        self.write_out("---back---")
        new_prod = self.stack.pop()
        self.input_stack.append(new_prod)
        self.current_position -= 1
        print(self.stack)

    def success(self):
        # Prints "---success---" and changes the state of the parser to "f".
        self.write_out("---success---")
        self.state = "f"

    def another_try(self):
        '''
        changes the state of the parser to "q".
        It pops the last item from the working stack and checks if the production number is less than the number of productions for          the non-terminal.
        If it is, it appends a new tuple to the working stack and changes the production on the top of the input stack.
        If the index is 0 and the last item is the start symbol, the state is changed to "e".
        Otherwise, the last production is removed from the input stack and replaced with the last non-terminal.
        :return:
        '''
        #used when head of working stack is a nonterm
        self.write_out("---another try---")
        last = self.stack.pop()  # (last, production_nr)
        if last[1] + 1 < len(self.grammar.get_productions_for_nonterm(last[0])):
            self.state = "q"
            # put working next production for the symbol
            new_tuple = (last[0], last[1] + 1)
            self.stack.append(new_tuple)

            # change production on top input
            length_last_production = len(self.grammar.get_productions_for_nonterm(last[0])[last[1]])
            # delete last production from input
            self.input_stack = self.input_stack[length_last_production:]

            # put new production in input
            new_production = self.grammar.get_productions_for_nonterm(last[0])[last[1] + 1]
            self.input_stack = new_production + self.input_stack
        elif self.current_position == 0 and last[0] == self.grammar.getS()[0]:

            print(self.current_position)
            self.state = "e"
        else:
            # change production on top input
            length_last_production = len(self.grammar.get_productions_for_non_terminal(last[0])[last[1]])
            # delete last production from input
            self.input_stack = self.input_stack[length_last_production:]
            self.input_stack.append([last[0]])

    def print_working(self):
        # prints the working stack to the screen and in the output file
        print(self.stack)
        self.write_out(str(self.stack))

    def run(self, sequence):
        '''1. The function checks if the state is not equal to 'f' or 'e'
        2. The function write all data to the output file
        3. If the state is equal to 'q', it checks if the input stack is empty and if the current position is equal to the length of the input string
        4. If the input stack is empty and the current position is equal to the length of the input string, the success function is called
        5. If the input stack is empty but the current position is not equal to the length of the input string, the momentary insuccess function is called
        6. If the input stack is not empty and its head is a non terminal, the expand function is called
        7. If the index is less than the length of the input string and the head of the input stack is equal to the current symbol from the input, the advance function is called
        8. If none of the previous conditions is true, the momentary insuccess function is called
        9. If the state is equal to 'b', it checks if the working stack's last element is a terminal '''
        while (self.state != 'f') and (self.state != 'e'):
            self.write_all_data()
            if self.state == 'q':
                if len(self.input_stack) == 0 and self.current_position == len(sequence):
                    self.success()
                elif len(self.input_stack) == 0:
                    self.momentary_insuccess()
                elif self.input_stack[0] in self.grammar.getN():
                    self.expand()
                    # WHEN: head of input stack is a non terminal

                elif self.current_position < len(sequence) and self.input_stack[0] == sequence[self.current_position]:
                    self.advance()
                else:
                    # WHEN: head of input stack is a terminal ≠ current symbol from input
                    self.momentary_insuccess()

            elif self.state == 'b':
                if self.stack[-1] in self.grammar.getE():
                    self.back()
                else:
                    self.another_try()

        if self.state == 'e':
            message = "Error at position : {}".format(self.current_position)
        else:
            message = "Sequence is accepted!"
            self.print_working()

        print(message)
        self.write_out(message, True)
        self.create_parsing_tree()
        self.parserOutput.write_parsing_tree()

    def create_parsing_tree(self):
        # creates the parsing tree
        '''
        Create an empty list called tree.
        Iterate through the working stack
        If the item in the working stack is a tuple, create a new Node object with the first element of the tuple as
        the node's value and append it to the tree list and sets the production rule to the second value of the tuple
        Otherwise, create a new Node object with the item as the node's value and append it to the tree list
        '''
        father = -1
        for index in range(0, len(self.stack)):
            # iterates in the working stack
            if type(self.stack[index]) == tuple:
                self.tree.append(Node(self.stack[index][0]))  # value
                self.tree[index].production = self.stack[index][1]
            else:
                self.tree.append(Node(self.stack[index]))

        '''
        Set the father of the Node to the index of the item in the working stack
        If the item in the working stack is a tuple, compute the length of the production of the non-terminal
        Create a vector of indexes corresponding to the length of the production
        For each item in the vector of indexes, if the corresponding tree node has a production, compute the length of its depth
        Add the computed length to the vector of indexes
        For each item in the vector of indexes, set the sibling of the corresponding tree node to the next item in the vector of indexes
        Set the father of the Node to -1
'''
        for index in range(0, len(self.stack)):
            if type(self.stack[index]) == tuple:
                self.tree[index].father = father  # sets the father
                father = index
                # computes the length of the production of a nonterm
                len_prod = len(
                    self.grammar.getP()[self.stack[index][0]][self.stack[index][1]])
                vector_index = []
                for i in range(1, len_prod + 1):
                    vector_index.append(index + i)
                for i in range(0, len_prod):
                    if self.tree[vector_index[i]].production != -1:
                        offset = self.get_length_depth(vector_index[i])
                        for j in range(i + 1, len_prod):
                            vector_index[j] += offset
                for i in range(0, len_prod - 1):
                    self.tree[vector_index[i]].sibling = vector_index[i + 1]
            else:
                self.tree[index].father = father
                father = -1

    def get_length_depth(self, index):
        '''
        get_length_depth() takes in an index parameter and obtains the corresponding production from the grammar.
        The length of the production is then determined and stored in the variable length_of_production.
        The sum of the length of the production is calculated and stored in the variable sum.
        A for loop is then used to iterate through the elements in the production and, if the element is a tuple, the get_length_depth() function is called again with the index of the element as its parameter.
        The result of the get_length_depth() function is then added to the sum.
        The sum is then returned.
        '''
        production = self.grammar.getP()[self.stack[index][0]][self.stack[index][1]]
        length_of_production = len(production)
        sum = length_of_production
        for i in range(1, length_of_production + 1):
            if type(self.stack[index + i]) == tuple:
                sum += self.get_length_depth(index + i)
        return sum




