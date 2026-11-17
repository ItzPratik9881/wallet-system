import {

Chart as ChartJS,

ArcElement,

Tooltip,

Legend

} from "chart.js";

import { Doughnut } from "react-chartjs-2";

ChartJS.register(

ArcElement,

Tooltip,

Legend

);

const ChartSection=({dashboard})=>{

const data={

labels:[

"Deposits",

"Withdrawals",

"Transfers"

],

datasets:[

{

data:[

dashboard.depositCount,

dashboard.withdrawCount,

dashboard.transferCount

],

backgroundColor:[

"#22c55e",

"#f59e0b",

"#7c3aed"

],

borderWidth:0

}

]

};

const options={

plugins:{

legend:{

position:"bottom"

}

},

cutout:"72%"

};

return(

<div className="chart-card">

<h2>

Transaction Analytics

</h2>

<div className="chart-wrapper">

<Doughnut

data={data}

options={options}

/>

</div>

</div>

);

};

export default ChartSection;