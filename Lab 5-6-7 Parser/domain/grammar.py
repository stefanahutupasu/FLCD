class Grammar:
    def __init__(self, N, E, P, S):
        self.N = N #non-terminals
        self.E = E #terminals
        self.P = P #productions(key is non-terminal and value is a set of non-terminals or terminals
        self.S = S #start symbols

    def getN(self):
        return self.N

    def getE(self):
        return self.E

    def getP(self):
        return self.P

    def getS(self):
        return self.S

    def is_nonterminal(self, value):
        return value in self.N

    def is_terminal(self, value):
        return value in self.E

    @staticmethod
    def parse_line(line):
        return [value.strip() for value in line.strip().split('=')[1].strip()[1:-1].strip().split(',')]

    @staticmethod
    def parse_productions(rules):
        result = {}
        index = 1

        for rule in rules:
            lhs, rhs = rule.split('->')
            lhs = lhs.strip()
            rhs = [value.strip() for value in rhs.split('|')]

            for value in rhs:
                if lhs in result.keys():
                    result[lhs].append((value, index))
                else:
                    result[lhs] = [(value, index)]
                index += 1

        return result

    @staticmethod
    def from_file(fileName):
        with open("grammar.txt", 'r') as file:
            N = Grammar.parse_line(file.readline())
            E = Grammar.parse_line(file.readline())
            S = file.readline().split('=')[1].strip()
            P = Grammar.parse_productions(Grammar.parse_line(''.join([line for line in file])))

        return Grammar(N, E, P, S)

    def get_productions_for_nonterm(self, nonterm):
        if not self.is_nonterminal(nonterm):
            raise Exception('Can only show productions for non-terminals')
        for key in self.P.keys():
            if key == nonterm:
                return self.P[key]

    def isCFG(self):
        for nonterm in self.N:
            if nonterm not in self.P.keys():
                return False

        for key in self.P.keys():
            if len(key) != 1:
                return False

        return True



    def __str__(self):
        return 'N = { ' + ', '.join(self.N) + ' }\n' \
               + 'E = { ' + ', '.join(self.E) + ' }\n' \
               + 'P = { ' + ', '.join([' -> '.join(prod) for prod in self.P]) + ' }\n' \
               + 'S = ' + str(self.S) + '\n'