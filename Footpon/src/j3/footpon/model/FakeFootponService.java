package j3.footpon.model;

import java.util.ArrayList;

public class FakeFootponService implements IFootponService {

	@Override
	public ArrayList<Footpon> getFootponsInArea(double longtitude, double latitude) {
		
		ArrayList<Footpon> footpons = new ArrayList<Footpon>();
		footpons.add(new Footpon("Nintendo World Store","Video Game","Hidden","10% NDS Game", 40.757942,-73.979478,40,1234l));
		footpons.add(new Footpon("Wendy's","Food","Hidden","Free small soda",40.758198,-73.981414,20, 0));
		footpons.add(new Footpon("Toys R Us","Toys","Hidden","Buy one LEGO set, get second LEGO set half off", 40.757210,-73.985679,90, 0));
		footpons.add(new Footpon("Midtown Bikes","Outdoor","Hidden","Buy one tire, get the second free", 40.761493,-73.990115,50, 0));		
	
		return footpons;
	}
	
	@Override
	public ArrayList<Footpon> getInstance() {
			return getFootponsInArea(0,0);
	}

}
