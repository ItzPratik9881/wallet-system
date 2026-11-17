import "../styles/SummaryCard.css";

function SummaryCard({ title, value, color }) {
  return (
    <div className={`summary-card ${color}`}>
      <h3>{value}</h3>
      <p>{title}</p>
    </div>
  );
}

export default SummaryCard;