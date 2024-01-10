export default class CancelError extends Error
{
	constructor(message)
	{
		super(message || "Cancelled");
	}
}
