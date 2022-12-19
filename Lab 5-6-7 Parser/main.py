from domain.grammar import Grammar
from domain.parser import Parser

grammar = Grammar.from_file("grammar.txt")

print(grammar.getN())
print(grammar.getE())
print(grammar.getP())
print(grammar.getS())
print(grammar.get_productions_for_nonterm('A'))
print(grammar.isCFG())


if __name__ == '__main__':
    # parser = ParserRecursiveDescendent("g2.txt", "PIF.out", "out2.txt")
    parser = Parser("g1.txt", "seq.txt", "out2.txt")
    # run the Parser for the sequence
    parser.run(parser.sequence)