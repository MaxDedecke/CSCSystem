package com.css.one.data;

import java.time.LocalDate;

import com.css.one.data.enums.GrowStatus;

public interface EntityWrapper {
	public String getNummer();
	public Long getId();
	public String getName();
	public LocalDate getErfasst();
	public boolean isCharge();
	public Location getLocation();
	public GrowStatus getStatus();
	public boolean hasElements();
}
