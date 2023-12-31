(ns blackjack.game
  (:require [card-ascii-art.core :as card]))


;A, 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K
;1....13
(defn new-card []
  "Generates a card number between 1 and 13"
  (inc (rand-int 13))
  )

; vai calcular os pontos de acordo com as cartas
; J, Q, K = 10 (não 11, 12 e 13)
; [A 10] = 11 ou 21 = 21
; [A 5 7] = 1+5+7 (13) ou 11+5+7 (24)
; A = 11 porem se passar de 21 ele vai valer 1

(defn JQK->10 [card]
  (if (> card 10) 10 card))

(defn A->11 [card]
  (if (= card 1) 11 card))

(defn points-cards [cards]
  (let [
        cards-without-JQK (map JQK->10 cards)
        cards-with-A11 (map A->11 cards-without-JQK)
        points-with-A-1 (reduce + cards-without-JQK)
        points-with-A-11 (reduce + cards-with-A11)
        ]
    (if (> points-with-A-11 21)
      points-with-A-1
      points-with-A-11
      )))


;representa o jogador
(defn player [player-name]
  (let [
        card-1 (new-card)
        card-2 (new-card)
        cards [card-1 card-2]
        points (points-cards cards)
        ]
    {:player-name player-name
     :cards       cards
     :points      points
     }))

; chamar a função new-card para gerar a nova carta
; atualizar o vetor cards dentro do player com a nova carta
; calcular os pontos do jogador com o novo vetor de cartas
; retornar esse novo jogador
(defn more-card [player]
  (let [card (new-card)
        cards (conj (:cards player) card)
        new-player (update player :cards conj card)
        points (points-cards cards)]
    (assoc new-player :points points)))

;(card/print-player (player "Pedro"))
;(card/print-player (player "Eduardo"))


(defn player-decision-continue? [player]
  (println (:player-name player) ": mais cartas?")
  (= (read-line) "sim"))

(defn dealer-decision-continue? [player-points dealer]
  (let [dealer-points (:points dealer)
        decision (<= dealer-points player-points)]
    (if (> player-points 21) false decision)))

; função game, responsavel por perguntar para o jogador se ele quer mais cartas
; caso ele queira mais cartas, chamar a função more-card
; e assim sucessivamente
(defn game [player fn-decision-continue]
  (if (fn-decision-continue player)
    (let [player-with-more-cards (more-card player)]
      (card/print-player player-with-more-cards)
      (recur player-with-more-cards fn-decision-continue))
    player))


; se ambos passaram de 12 -> ambos perderam
; se pontos igauis -> empatou
; se player passou de 21 -> dealer ganhou
; se dealer passou de 21 -> player ganhou
; se player maior que dealer -> player ganhou
; se dealer maior que player -> dealer ganhou
(defn end-game [player dealer]
  (let [player-points (:points player)
        player-name (:player-name player)
        dealer-points (:points dealer)
        dealer-name (:player-name dealer)
        message (cond (and (> player-points 21) (> dealer-points 21)) "Ambos perderam"
                      (= player-points dealer-points) "Empate"
                      (> player-points 21) (str dealer-name " ganhou")
                      (> dealer-points 21) (str player-name " ganhou")
                      (> dealer-points player-points) (str dealer-name " ganhou")
                      (> player-points dealer-points) (str player-name " ganhou"))]
    (card/print-player player)
    (card/print-player dealer)
    (println message)
    ))

(def player-1 (player "Pedro Marinho"))
(card/print-player player-1)

(def dealer (player "Dealer"))
(card/print-masked-player dealer)

(def player-after-game (game player-1 player-decision-continue?))
(def dealer-after-game (game dealer (partial dealer-decision-continue? (:points player-after-game))))

(end-game player-after-game dealer-after-game)