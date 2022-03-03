import './RestaurantCard.css';

export default function RestaurantCard(props) {
    return (
        <div className='card'>
            <div className='restaurant-image'/>
            <div className='restaurant-name'>Restaurant Name Here</div>
            <div className='restaurant-info'>"Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit..."</div>
        </div>
    )
}