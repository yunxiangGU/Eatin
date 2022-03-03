import RestaurantCard from "./RestaurantCard";


export default function RestaurantList(props) {
    let res = [];
    console.log("restaurants: ", props.restaurants)
    if (props.restaurants.length <= 0) {
        return null
    } else {
        for (let restaurant of props.restaurants) {
            console.log("restaurant: ", restaurant)
            res.push(<RestaurantCard key={Math.random().toString(36).substr(2, 9)} name={restaurant.name} desc={restaurant.desc}/>)
        }
        return res;
    }
}