;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname hw4a) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
(require 2htdp/image)
(require 2htdp/universe)

(define G-HEIGHT 100)
(define G-WIDTH 600)
(define G-SIZE 20)
(define G-COLOR "blue")
(define G-BACKG (empty-scene G-WIDTH G-HEIGHT))

; A number num is a Natural Number ranging from 0 to 9
; Interpretation: a number num represents a randomly generated number from 0 to 9
(define num (random 10))
(string-append "The random number to guess is: " (number->string num))

; num-temp : Number -> ???
#;(define (num-temp num)
    (...num...))

(define-struct guess [state totalguesses])
; a Guess is a (make-guess GuessNum Number)
; Interpretation: a (make-guess n tg) represents a GuessNum n
;   and the total number of guesses tg
; A State is either:
; - 1 if it's higher than num
; - -1 if it's lower than num
; - 0 if it's equal to num
; - "start" if no guesses have been inputed
; Interpretation: a State represents if the user's guess was higher than, 
;   lower than, or equal to the randomly generated number num (or "start" if game has began)

; guess-temp : Guess -> ???
(define (guess-temp gt)
  (... (state-temp (guess-state gt)) ... (guess-totalguesses gt) ...))

; Examples
(define EQUAL-STATE 0)
(define HIGHER-STATE 1)
(define LOWER-STATE -1)
(define START-STATE "Start")

(define num-guesses1 1)
(define num-guesses2 2)
(define num-guesses3 3)
(define num-guesses4 0)

(define guess1 (make-guess EQUAL-STATE num-guesses1))
(define guess2 (make-guess HIGHER-STATE num-guesses2))
(define guess3 (make-guess LOWER-STATE num-guesses3))
(define guess4 (make-guess START-STATE num-guesses4))

; state-temp : Natural (0 to 9) -> ???
#;(define (state-temp state)
    (cond [(string? state) ...]
          [(= state 0) ...]
          [(= state 1) ...]
          [(= state -1) ...]))

(define START-CLIENT-GUESS (make-guess "start" 0))

; guess-game : Guess -> Number
; check if the guess equals the random number and outputs total number of guesses 
(define (guess-game guess)
  (guess-totalguesses (big-bang guess
                        [to-draw draw-game]
                        [on-key check-guess]
                        [stop-when correct-guess? draw-game])))

(define START-TEXT "Guess!")
(define HIGH-TEXT "Nope, lower.")
(define LOW-TEXT "Nope, higher.")
(define EQUAL-TEXT "You've, won!")

; draw-game : Guess -> Image
; draws the game based on the current game state
(define (draw-game guess)
  (cond [(string? (guess-state guess)) (create-text START-TEXT)]
        [(= (guess-state guess) 0) (create-text EQUAL-TEXT)]
        [(= (guess-state guess) 1) (create-text HIGH-TEXT)]
        [(= (guess-state guess) -1) (create-text LOW-TEXT)]))

; create-text : Guess -> Image
; creates the image text based on Guess Fields
(define (create-text str)
  (overlay (text str G-SIZE G-COLOR)
           G-BACKG))

; check-guess : Guess KeyEvent -> Guess
; Determines the next state of the game by comparing the
; inputted number to Num
(define (check-guess guess ke)
  (cond  [(key=? ke (number->string num)) (make-guess 0 (+ 1 (guess-totalguesses guess)))]
         [(> (string->number ke) num) (make-guess 1 (+ 1 (guess-totalguesses guess)))]
         [(< (string->number ke) num) (make-guess -1 (+ 1 (guess-totalguesses guess)))]
         [(string? (guess-state guess)) guess]))

; (check-expect (check-guess guess1) (make-guess 0 1)) -> can't check expect b/c can't
;   predict the random number

; correct-guess? : Guess -> Boolean
; Checks if the Guess-State is equal to zero
; which signifies the game is over
(define (correct-guess? guess)
  (if (equal? (guess-state guess) EQUAL-STATE) #true #false))

; Test
(check-expect (correct-guess? guess1) #true)
(check-expect (correct-guess? guess2) #false)
(check-expect (correct-guess? guess3) #false)
(check-expect (correct-guess? guess4) #false)

; Test
(guess-game START-CLIENT-GUESS)

;====================================================================

(define-struct words (count newword longest))
; Words is (make-words Number OptString OptString)
; OptString is one of: #false or String
; Interpretation: a (make-words c w l) represents a message with
;   count c words left to generate, newword w, and longest word l

; Examples:
(define word0 (make-words 10 #false #false))
(define word1 (make-words 5 "hello" "infamous"))
(define word2 (make-words 4 "world" "infamous"))

; os-temp
#;(define (os-temp os)
    (cond
      [(false? os) ...]
      [else os ...]))

(define HOST "dictionary.ccs.neu.edu")
(define PORT 10000)

; client: Natural -> Words
; receives n words, displays "none", the word received with the
;  largest length or the most recent word if it creates a tie
(define (client n)
  (big-bang (make-words n #false #false)
    ;--------------------------
    [register HOST]
    [port PORT]
    [on-receive receive-word]
    ;--------------------------
    [stop-when count-below-0? render-longest-word]
    [to-draw render-longest-word]))

(define FONT-SIZE 22)
(define FONT-COLOR "red")
(define WORD-BACKG (empty-scene 400 100))

; render-longest-word : Words -> Image
; render the longest word received so far
;   or the most recent word tied in length
(define (render-longest-word s)
  (overlay (text (as-string (words-longest s )) FONT-SIZE FONT-COLOR)
           WORD-BACKG))

; receive-word : Words  String -> Words
; counts word and remembers it as well as keeps track of longest running word
(define (receive-word w msg)
  (make-words (- (words-count w) 1 ) msg (longer-word msg (as-string(words-longest w)))))

; longer-word : String String -> String
; determines the longer string out of two strings and picks the first one if equal in length
; Checks
(check-expect (longer-word "hi" "there") "there")
(check-expect (longer-word "hello" "world") "hello")
(define (longer-word w1 w2)
  (if (>= (string-length w1) (string-length w2))
      w1
      w2))

; Check
(check-expect (receive-word (make-words 5 "hi" "hi") "there")
              (make-words 4 "there" "there"))

; as-string : OptString -> String
; maps #false to "" and every string to itself
(define (as-string os)
  (cond
    [(false? os) ""]
    [else os]))

; Checks
(check-expect (as-string #false) "")
(check-expect (as-string "hi") "hi")

; count-below-0?: Words -> Boolean
; is the count field below 0?
(define (count-below-0? w)
  (< (words-count w) 0))

; Checks
(check-expect (count-below-0? (make-words -1 "hello" "hello")) #true)
(check-expect (count-below-0? (make-words +1 "worlds?" "worlds?")) #false)

;test
(client 20)

;===================================================================

; A Building is one of:
; - "ground"
; - (make-story Number PosInt String Building)
(define-struct story [height rooms color below])
; and represents either the ground story (no height,
;    rooms, color, or stories below it,
; OR a story with a height (starting at 1), number of
;    rooms, color, and the rest of the building beneath it

; Examples
(define building-0 "ground")
(define building-1 (make-story 55 3 "red" building-0))
(define building-2 (make-story 50 5 "green" building-1))
(define building-3 (make-story 60 7 "blue" building-2))

; building-temp : Building -> ???
#;(define (building-temp building)
    (cond
      [(string? building) ...]
      [(story? building) (...
                          (story-height building)
                          (story-rooms building)
                          (story-color building)
                          (building-temp(story-below building)))]))

; total-rooms : Building -> Natural
; counts the total number of rooms in a building
(define (total-rooms building)
  (cond
    [(string? building) 0]
    [(story? building) (+
                        (story-rooms building) (total-rooms(story-below building)))]))

; Checks
(check-expect (total-rooms building-0) 0)
(check-expect (total-rooms building-1) 3)
(check-expect (total-rooms building-2) 8)
(check-expect (total-rooms building-3) 15)

(define BUILDING-WIDTH 120)
(define GROUND empty-image)

; draw-building : Building -> Image
; Draw a building based on its specifications
(define (draw-building building)
  (cond [(string? building) GROUND]
        [(story? building) (above (create-floor-rect building (story-rooms building))
                                  (draw-building (story-below building)))]))

; create-floor-rect : Building Natural -> Image
; Creates a row of rooms based on the specifications of the building
(define (create-floor-rect building num-rooms)
  (if (> num-rooms 1)
      (beside (create-building-rect building) (create-floor-rect building (sub1 num-rooms)))
      (beside (create-building-rect building) empty-image)))

; create-building-rect : Building -> Image
; Create a rectangle from the Building fields
(define (create-building-rect building)
  (frame (rectangle (/ BUILDING-WIDTH (story-rooms building))
                    (story-height building)
                    "solid" (story-color building))))

; Tests
(draw-building building-0)
(draw-building building-1)
(draw-building building-2)
(draw-building building-3)