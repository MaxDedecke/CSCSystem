package com.css.one.data;

import java.time.LocalDate;

public interface EntityWrapper {
	public String getNummer();
	public Long getId();
	public String getName();
	public LocalDate getErfasst();
	public boolean isCharge();
	public Location getLocation();
	public GrowStatus getStatus();
}
