;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-beginner-reader.ss" "lang")((modname hw4bfinal) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
; an OptPosn is one of:
; - #false
; - Posn
; an OptPosn represents either a position of false if the position is illegal or empty
(define OP-FALSE #false)
(define op1 (make-posn 0 0))
(define op2 (make-posn 1 0))
(define op3 (make-posn 0 1))
(define op4 (make-posn 99 99))
(define op5 (make-posn 98 99))
(define op6 (make-posn 99 98))
(define op7 (make-posn 50 50))
(define op8 (make-posn 51 50))
(define op9 (make-posn 50 51))
(define op10 (make-posn 49 50))
(define op11 (make-posn 50 49))
; op-temp : OptPosn -> ???
#;
(define (op-temp op)
  (cond
    [(false? op) ...]
    [(posn? op) ... (posn-x op) ... (posn-y op) ...]))

(define-struct neighbor [north south east west])
; a Neighbor is (make-neighbor OptPosn OptPosn OptPosn OptPosn)
; a (make-neighbor n s e w) represents 4 OptPosns, OptPosn n to the north, OptPosn s to the south
;   OptPosn e to the east, and OptPosn w to the west
(define n1 (make-neighbor op3 OP-FALSE op2 OP-FALSE))
(define n2 (make-neighbor OP-FALSE op6 OP-FALSE op5))
(define n3 (make-neighbor op9 op11 op8 op10))
; neighbor-temp : Neighbor -> ???
#;
(define (neighbor-temp n)
  ( ... (op-temp (neighbor-north n)) ... (op-temp (neighbor-south n))
        ... (op-temp (neighbor-east n)) ... (op-temp (neighbor-west n))))

; posn-neighbor : Posn -> Neighbor
; Interpretation: determines the four surrounding coordinates that neighbor the inputed position 
(define (posn-neighbor p)
  (make-neighbor (create-neighbor-n p)
                 (create-neighbor-s p)
                 (create-neighbor-e p)
                 (create-neighbor-w p)))
; Tests
(check-expect (posn-neighbor op1) n1)
(check-expect (posn-neighbor op4) n2)
(check-expect (posn-neighbor op7) n3)

; create-neighbor-n : Posn -> Posn
; Creates the neighbor to the north of a given posn
(define (create-neighbor-n p)
  (if (check-valid (make-posn (posn-x p) (add1 (posn-y p))))
      (make-posn (posn-x p) (add1 (posn-y p)))
      #false))
; Tests
(check-expect (create-neighbor-n op1) op3)
(check-expect (create-neighbor-n op4) OP-FALSE)

; create-neighbor-s : Posn -> Posn
; Creates the neighbor to the south of a given posn
(define (create-neighbor-s p)
  (if (check-valid (make-posn (posn-x p) (sub1 (posn-y p))))
      (make-posn (posn-x p) (sub1 (posn-y p)))
      #false))
; Tests
(check-expect (create-neighbor-s op1) OP-FALSE)
(check-expect (create-neighbor-s op4) op6)

; create-neighbor-e : Posn -> Posn
; Creates the neighbor to the east of a given posn
(define (create-neighbor-e p)
  (if (check-valid (make-posn (add1 (posn-x p)) (posn-y p)))
      (make-posn (add1 (posn-x p)) (posn-y p))
      #false))
; Tests
(check-expect (create-neighbor-e op1) op2)
(check-expect (create-neighbor-e op6) OP-FALSE)

; create-neighbor-w : Posn -> Posn
; Creates the neighbor to the west of a given posn
(define (create-neighbor-w p)
  (if (check-valid (make-posn (sub1 (posn-x p)) (posn-y p)))
      (make-posn (sub1 (posn-x p)) (posn-y p))
      #false))
; Tests
(check-expect (create-neighbor-w op1) OP-FALSE)
(check-expect (create-neighbor-w op4) op5)
  
; check-valid : Posn -> Boolean
; Determines if th neighbor to a posn is valid (x, y between -1 and 100)
(define (check-valid p)
  (and (and (< (posn-x p) 100) (> (posn-x p) -1))
       (and (< (posn-y p) 100) (> (posn-y p) -1))))


;==========================================================================================


; A Cost is one of:
; - (make-unit Number)
; - (make-lb Number)
(define-struct unit [cost])
(define-struct lb [cost])
; and represents either the cost per unit or per lb of an item
(define cost-1 (make-unit 0.3))
(define cost-2 (make-lb 2.5))
(define cost-3 (make-unit 4))
; cost-temp : Cost -> ???
#;
(define (cost-temp c)
  (cond
    [(unit? c) ... (unit-cost c) ...]
    [(lb? c) ... (lb-cost c) ...]))

; A CE (CatalogueEntry) is a
; (make-ce String Cost)
(define-struct ce [name cost])
; and represents the name of a food and how much it costs
(define ce-1 (make-ce "banana" cost-1))
(define ce-2 (make-ce "carrot" cost-2))
(define ce-3 (make-ce "cereal" cost-3))
; ce-temp : CE -> ???
#;
(define (ce-temp ce)
  ( ... (ce-name ce) ... (cost-temp(ce-cost ce)) ...))

; A GC (GroceryCatalogue) is one of:
; - '()
; - (cons CE GC)
; where each catalogue entry has a unique name
(define gc-1 '())
(define gc-2 (cons ce-1 '()))
(define gc-3 (cons ce-2 gc-2))
(define gc-4 (cons ce-3 gc-3))
; gc-temp : GC -> ???
#; 
(define (gc-temp gc)
  (cond
    [(empty? gc) ...]
    [(cons? gc) ... (ce-temp(first gc)) ... (gc-temp(rest gc)) ...]))

; A Order is one of:
; - String
; - (make-weight String Number)
(define-struct weight [name lb])
; and represents either one unit of food or its name and weight in lbs
(define order-1 (make-weight "steak" 5))
(define order-2 "chicken")
(define order-3 (make-weight "tomatoes" 2))
(define order-4 "banana")
(define order-5 "cereal")
(define order-6 "carrot")
; order-temp : Order -> ???
#;
(define (order-temp o)
  (cond
    [(string? o) ...]
    [(weight? o) ... (weight-name o) ... (weight-lb o) ...]))

; A Checkout is one of:
; - '()
; - (cons Order Checkout)
(define cko-1 '())
(define cko-2 (cons order-1 cko-1))
(define cko-3 (cons order-2 cko-2))
(define cko-4 (cons order-3 cko-3))
(define cko-5 (cons order-4 (cons order-5 '())))
; cko-temp : Checkout -> ???
#;
(define (cko-temp cko)
  (cond
    [(empty? cko) ...]
    [(cons? cko) ... (order-temp(first cko)) ... (cko-temp(rest cko)) ...]))

(define ERROR-404 "Grocery not found (in the specified Grocery Catalogue).")

; get-cost : String GC -> Cost
; returns the Cost of a given food in a given GroceryCatalogue
(define (get-cost name gc)
  (cond
    [(empty? gc) (error ERROR-404)]
    [(cons? gc) (if (food-exists? name (first gc))
                    (ce-cost(first gc))
                    (get-cost name (rest gc)))]))
(check-error (get-cost "banana" gc-1) ERROR-404)
(check-expect (get-cost "banana" gc-4) cost-1)
(check-expect (get-cost "carrot" gc-3) cost-2)

; food-exists? : String CE -> Boolean
; checks if the given food name matches that of the CE
(define (food-exists? name ce)
  (string=? name (ce-name ce)))
(check-expect (food-exists? "banana" ce-1) #true)
(check-expect (food-exists? "banana" ce-2) #false)

(define newcost1 (make-unit 0.5))
(define newcost2 (make-lb 5))

; set-cost : String Cost GC -> GC
; returns a catalogue with the given food changed to the new given cost
(define (set-cost name cost gc)
  (cond
    [(empty? gc) '()]
    [(cons? gc) (cons (change-cost name cost (first gc)) (set-cost name cost (rest gc)))]))
(check-expect (set-cost "carrot" newcost2 gc-4)
              (cons ce-3 (cons (make-ce "carrot" newcost2) (cons ce-1 '()))))

; change-cost : String Cost CE -> CE
; changes the cost of a given CE to the new given cost if the food name is the same
(define (change-cost name cost ce)
  (if (food-exists? name ce)
      (make-ce name cost)
      ce))
(check-expect (change-cost "banana" newcost1 ce-1) (make-ce "banana" newcost1))
(check-expect (change-cost "banana" newcost2 ce-2) ce-2)

(define ERROR-UNITS "No items in the catalogue are priced by unit.")
(define cost-4 (make-lb 6.9))
(define ce-4 (make-ce "shrimp" cost-4))
(define gc-5 (cons ce-4 (cons ce-2 '())))

; average-unit-cost : GC -> Number
; produces average cost of items priced per unit in the grocery catalogue
(define (average-unit-cost gc)
  (cond
    [(empty? gc) 0]
    [(cons? gc) (if (> (total-units gc) 0)
                    (/ (+ (get-unit-cost(first gc)) (average-unit-cost(rest gc))) (total-units gc))
                    (error ERROR-UNITS))]))
(check-expect (average-unit-cost gc-1) 0)
(check-expect (average-unit-cost gc-4) 2.15)
(check-error (average-unit-cost gc-5) ERROR-UNITS)

; get-unit-cost : CE -> Number
; returns the price of a ce if it is priced per unit
(define (get-unit-cost ce)
  (if (unit? (ce-cost ce))
      (unit-cost (ce-cost ce))
      0))
(check-expect (get-unit-cost ce-2) 0)
(check-expect (get-unit-cost ce-1) 0.3)

; total-units : GC -> Number
; calculates the number of units in a gc
(define (total-units gc)
  (cond
    [(empty? gc) 0]
    [(cons? gc)(+
                (if (unit? (ce-cost(first gc)))
                    1
                    0)
                (total-units(rest gc)))]))
(check-expect (total-units gc-2) 1)
(check-expect (total-units gc-4) 2)

(define cko-halfplsno (cons "cake"
                            (cons "cake"
                                  (cons "cake"
                                        (cons "cake"
                                              (cons "cake" '()))))))
(define cko-plsno (cons "cake"
                        (cons "cake"
                              (cons "cake"
                                    (cons "cake"
                                          (cons "cake"
                                                (cons "cake" cko-halfplsno)))))))

; express-lane? : Checkout -> Boolean
; determines if a Checkout has 10 or fewer items
(define (express-lane? cko)
  (cond
    [(empty? cko) #false]
    [(cons? cko) (<= (express-count cko) 10)]))
(check-expect (express-lane? cko-plsno) #false)
(check-expect (express-lane? cko-1) #false)
(check-expect (express-lane? cko-4) #true)

; express-count : Checkout -> Number
; determines how many items the checkout has if it has at least 1 item
(define (express-count cko)
  (cond
    [(empty? cko) 0]
    [(cons? cko) (add1 (express-count(rest cko)))]))
(check-expect (express-count cko-1) 0)
(check-expect (express-count cko-4) 3)

(define ERROR-NONE "No items in checkout or catalogue.")
(define ERROR-WEIGHTITEM "An item is priced by weight, so the total price cannot be totalled.")

; total-cost : Checkout GC -> Number
; determines total cost of items in the Checkout
(define (total-cost cko gc)
  (cond
    [(or (empty? cko) (empty? gc)) (error ERROR-NONE)]
    [(cons? cko) (determine-cost cko gc)]))
(check-expect (total-cost cko-5 gc-4) 4.3)
(check-error (total-cost cko-5 gc-1) ERROR-NONE)
(check-error (total-cost cko-1 gc-4) ERROR-NONE)

; determine-cost : Checkout GC -> Number
; determines total cost of items in checkout if checkout and catalogue are nonempty
(define (determine-cost cko gc)
  (cond
    [(empty? cko) 0]
    [(cons? cko) (+ (get-cost-if-item (first cko) gc) (determine-cost(rest cko) gc))]))
(check-expect (determine-cost cko-5 gc-4) 4.3)

; get-cost-item : Order GC -> Number
; gets cost checking to make sure unit isn't weight defined
; get-cost function defined on ln173.
(define (get-cost-if-item order gc)
  (if (string? order)
      (check-price order gc)
      (error ERROR-WEIGHTITEM)))
(check-expect (get-cost-if-item order-4 gc-4) 0.3)
(check-error (get-cost-if-item order-3 gc-4) ERROR-WEIGHTITEM)

; check-price : Order(String) CE -> Number
; returns cost of unit-priced item, error for weight-defined items
; get-cost function defined on ln173.
(define (check-price order gc)
  (if (lb? (get-cost order gc))
      (error ERROR-WEIGHTITEM)
      (unit-cost (get-cost order gc))))
(check-expect (check-price order-5 gc-4) 4)
(check-error (check-price order-6 gc-4) ERROR-WEIGHTITEM) 