(ns blackjack.test1)


(def nome "Pedro")
(def idade 21)
(def numeros [1 2 3])
(println numeros)

(defn saudacao
  "Saudaçao iniciante em Cloujure"
  [name]
  (str "Bem vindo " name))


(println (saudacao nome))