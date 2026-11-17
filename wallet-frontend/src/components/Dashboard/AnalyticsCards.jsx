import {

FaWallet,

FaArrowDown,

FaArrowUp,

FaExchangeAlt

} from "react-icons/fa";

const AnalyticsCards = ({ dashboard }) => {

const cards=[

{

title:"Wallet Balance",

value:`₹ ${dashboard.balance}`,

icon:<FaWallet/>,

color:"#2563eb"

},

{

title:"Deposits",

value:dashboard.depositCount,

icon:<FaArrowDown/>,

color:"#22c55e"

},

{

title:"Withdrawals",

value:dashboard.withdrawCount,

icon:<FaArrowUp/>,

color:"#f59e0b"

},

{

title:"Transfers",

value:dashboard.transferCount,

icon:<FaExchangeAlt/>,

color:"#7c3aed"

}

];

return(

<div className="cards-grid">

{

cards.map((card,index)=>(

<div

className="analytics-card"

key={index}

>

<div

className="card-icon"

style={{

background:card.color

}}

>

{card.icon}

</div>

<h4>

{card.title}

</h4>

<h2>

{card.value}

</h2>

</div>

))

}

</div>

);

};

export default AnalyticsCards;