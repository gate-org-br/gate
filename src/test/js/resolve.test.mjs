import assert from 'node:assert/strict';
import test from 'node:test';

import resolve from '../../main/resources/META-INF/resources/gate/resolve.js';

test('preserves an already encoded URL', () =>
{
	assert.equal(
		resolve(null, {}, '/Gate?q=Jo%C3%A3o%20Silva'),
		'/Gate?q=Jo%C3%A3o%20Silva');
});

test('leaves static URL text unchanged', () =>
{
	assert.equal(
		resolve(null, {}, '/Gate?q=João Silva'),
		'/Gate?q=João Silva');
});

test('resolves an encoded expression without double encoding the URL', () =>
{
	assert.equal(
		resolve(null, {detail: {name: 'João Silva'}},
			'/Gate?q=%40prop%28detail.name%29&return=%2Fhome%20page'),
		'/Gate?q=Jo%C3%A3o%20Silva&return=%2Fhome%20page');
});

test('resolves an unencoded expression', () =>
{
	assert.equal(
		resolve(null, {detail: {id: 42}}, '/Gate?id=@prop(detail.id)'),
		'/Gate?id=42');
});

test('encodes reserved characters produced by an expression', () =>
{
	assert.equal(
		resolve(null, {detail: {name: 'João & Maria?#'}},
			'/Gate?q=@prop(detail.name)&page=1'),
		'/Gate?q=Jo%C3%A3o%20%26%20Maria%3F%23&page=1');
});
