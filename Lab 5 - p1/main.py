from domain.grammar import Grammar

grammar = Grammar.from_file("grammar.txt")

print(grammar.getN())
print(grammar.getE())
print(grammar.getP())
print(grammar.getS())
print(grammar.get_productions_for_nonterm('A'))
print(grammar.isCFG())