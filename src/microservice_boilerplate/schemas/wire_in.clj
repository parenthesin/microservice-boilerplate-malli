(ns microservice-boilerplate.schemas.wire-in)

(def WalletDeposit
  [:map
   [:btc number?]])

(def WalletWithdrawal
  [:map
   [:btc number?]])

(def WalletEntry
  [:map
   [:id uuid?]
   [:btc-amount number?]
   [:usd-amount-at number?]
   [:created-at inst?]])

(def WalletHistory
  [:map
   [:entries [:vector WalletEntry]]
   [:total-btc number?]
   [:total-current-usd number?]])

(def BtcUsdPair
  {:btc-amount number?
   :usd-amount number?})
