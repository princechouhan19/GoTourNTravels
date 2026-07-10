const router = require('express').Router();
const { asyncHandler, success } = require('../utils/helpers');

/**
 * Returns nearby Points of Interest around Mount Abu.
 * In production this proxies Google Places API; here we return a static
 * curated dataset (see seed/places.js) so the app works without a Maps key.
 */
router.get('/', asyncHandler(async (req, res) => {
  const { category } = req.query;
  const places = require('../seed/places');
  const items = category ? places.filter((p) => p.category === category) : places;
  success(res, { items });
}));

router.get('/categories', asyncHandler(async (req, res) => {
  const places = require('../seed/places');
  const cats = [...new Set(places.map((p) => p.category))];
  success(res, { categories: cats });
}));

module.exports = router;
