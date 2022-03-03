import './RestaurantCard.css';

export default function RestaurantCard(props) {
    console.log("name and desc: ", props.name, props.desc)
    return (
        <div className='card'>
            <div className='restaurant-image'/>
            <div className='restaurant-name'>{props.name}</div>
            <div className='restaurant-info'>{props.desc}</div>
        </div>
    )
}