class Node:
    def __init__(self, value):
        '''
        #The Node class is used to represent a node in the parse tree of a grammar. Each node has a value, a father (the node above it in the tree), and a sibling (the node beside it in the tree). It also includes a production attribute, which is used to indicate which production rule the node is based on. The Node class is used to store information about the parse tree and can be used to traverse the tree to generate an output.
        :param value:
        '''
        self.father = -1
        self.sibling = -1
        self.value = value
        self.production = -1

    def __str__(self):
        return str(self.value) + "  " + str(self.father) + "  " + str(self.sibling)